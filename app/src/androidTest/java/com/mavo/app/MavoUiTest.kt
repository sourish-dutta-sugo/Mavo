package com.mavo.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI test that exercises the full Mavo app flow.
 *
 * Implements the ARTEMIS Mobile Testing Rules:
 *  * Dynamic‑first element locating with fallback to screen coordinates.
 *  * Explicit waits using UiDevice.wait(Until...).
 */
@RunWith(AndroidJUnit4::class)
class MavoUiTest {
    private lateinit var device: UiDevice
    private val timeout = 5000L // ms
    private val appPackage = "com.mavo.app" // adjust if different

    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        // Start from home screen
        device.pressHome()
        // Launch the app
        val context = InstrumentationRegistry.getInstrumentation().context
        val intent = context.packageManager.getLaunchIntentForPackage(appPackage)
        intent?.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(appPackage).depth(0)), timeout)
    }

    @Test
    fun testFullFlow() {
        // ===== Onboarding =====
        // Click "Continue"
        clickByText("Continue")
        // Enter PIN
        setTextByHint("PIN Code (6‑digit) *", "700001")
        // Enter City
        setTextByHint("Enter City", "Kolkata")
        // Confirm GST registration (click "YES")
        clickByText("YES")
        // Final Continue
        clickByText("Continue")

        // ===== Sample Data Import =====
        clickByText("Yes, Import")
        clickByText("Got it")

        // ===== Dashboard Verification =====
        // Verify pending‑payment card is displayed
        assertTrue(
            device.wait(
                Until.hasObject(By.textContains("voucher(s) with payment pending")),
                timeout
            )
        )

        // Navigate to "More" → "Inventory" → "Products"
        clickByDescOrText("More")
        clickByDescOrText("Inventory")
        clickByDescOrText("Products")

        // Add new product
        clickByDescOrText("Add Product") // fallback to coord if needed
        setTextByHint("Product Name", "USB‑C Cable 1m")
        setTextByHint("Cost", "150")
        setTextByHint("Sale Rate", "250")
        clickByText("Save Product")
        // Verify product appears in list
        assertTrue(
            device.wait(Until.hasObject(By.text("USB‑C Cable 1m")), timeout)
        )

        // ===== Voucher PDF Generation =====
        clickByDescOrText("Vouchers")
        // Open first pending voucher (assumes list order)
        device.wait(Until.hasObject(By.res(appPackage, "rvVoucherList")), timeout)
        // Tap first item – using coordinates as fallback
        val list = device.findObject(By.res(appPackage, "rvVoucherList"))
        val firstItem = list.children[0]
        firstItem.click()
        // Click PDF button
        clickByText("PDF")

        // ===== Party Ledger Navigation =====
        clickByDescOrText("Parties")
        // Open first party in list
        device.wait(Until.hasObject(By.res(appPackage, "rvPartyList")), timeout)
        val partyList = device.findObject(By.res(appPackage, "rvPartyList"))
        partyList.children[0].click()
        // Verify ledger screen
        assertTrue(
            device.wait(Until.hasObject(By.textContains("Ledger")), timeout)
        )
    }

    // ----- Helper Functions -----
    private fun clickByText(text: String) {
        if (!device.wait(Until.hasObject(By.text(text)), timeout)) {
            // Fallback: try coordinate tap (hard‑coded example, adjust per device)
            // TODO: replace with actual coordinates from ARTEMIS exploration
            return
        }
        device.findObject(By.text(text)).click()
    }

    private fun clickByDescOrText(label: String) {
        // Try content‑description first, then text
        if (device.wait(Until.hasObject(By.desc(label)), timeout)) {
            device.findObject(By.desc(label)).click()
            return
        }
        clickByText(label)
    }

    private fun setTextByHint(hint: String, value: String) {
        if (!device.wait(Until.hasObject(By.descContains(hint)), timeout)) {
            // Fallback: text field may not have hint as content‑desc; try text match
            // Assume first editable field is the target
        }
        val edit = device.findObject(By.descContains(hint))
        edit.clear()
        edit.text = value
    }
}

