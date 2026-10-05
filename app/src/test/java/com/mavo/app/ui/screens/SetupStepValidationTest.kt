package com.mavo.app.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SetupStepValidationTest {

    @Test
    fun basicStep_requiresBusinessNameAndAddressButIgnoresPin() {
        assertEquals(
            listOf(RequiredBusinessField.BusinessName, RequiredBusinessField.Address),
            missingFieldsFor(SetupStep.Basic, businessName = "", address = "", pinCode = "")
        )
        assertEquals(
            listOf(RequiredBusinessField.Address),
            missingFieldsFor(SetupStep.Basic, businessName = "Mavo Traders", address = "", pinCode = "")
        )
        assertTrue(
            missingFieldsFor(SetupStep.Basic, businessName = "Mavo Traders", address = "1 Main St", pinCode = "")
                .isEmpty()
        )
    }

    @Test
    fun locationStep_requiresPinOnly() {
        assertEquals(
            listOf(RequiredBusinessField.PinCode),
            missingFieldsFor(SetupStep.Location, businessName = "", address = "", pinCode = "")
        )
        assertTrue(
            missingFieldsFor(SetupStep.Location, businessName = "", address = "", pinCode = "400001").isEmpty()
        )
    }

    @Test
    fun nonSetupSteps_neverBlockNavigation() {
        val steps = listOf(
            SetupStep.Onboarding1,
            SetupStep.Onboarding2,
            SetupStep.Terms,
            SetupStep.Permission,
            SetupStep.TaxBank
        )
        steps.forEach { step ->
            assertTrue(
                missingFieldsFor(step, businessName = "", address = "", pinCode = "").isEmpty()
            )
        }
    }

    @Test
    fun setupDraft_mapsEveryCollectedFieldOntoTheProfile() {
        val draft = SetupDraft(
            businessName = "Mavo Traders",
            ownerName = "Ravi",
            phone = "9000000001",
            altPhone = "9000000002",
            email = "ravi@mavo.test",
            address = "1 Main St",
            businessType = "Retailer",
            sellingType = "Products",
            pin = "400001",
            city = "Mumbai",
            state = "Maharashtra",
            stateCode = "27",
            gstEnabled = true,
            gstin = "27ABCDE1234F1Z5",
            pan = "ABCDE1234F",
            bankName = "HDFC",
            accountNo = "1234567890",
            ifsc = "HDFC0000123",
            bankBranch = "Andheri"
        )

        val profile = draft.toProfile()

        assertEquals("Mavo Traders", profile.businessName)
        assertEquals("Ravi", profile.ownerName)
        assertEquals("9000000001", profile.phone)
        assertEquals("9000000002", profile.altPhone)
        assertEquals("ravi@mavo.test", profile.email)
        assertEquals("1 Main St", profile.address)
        assertEquals("Retailer", profile.businessType)
        assertEquals("Products", profile.sellingType)
        assertEquals("400001", profile.pin)
        assertEquals("Mumbai", profile.city)
        assertEquals("Maharashtra", profile.state)
        assertEquals("27", profile.stateCode)
        assertEquals("27ABCDE1234F1Z5", profile.gstin)
        assertEquals("ABCDE1234F", profile.pan)
        assertEquals("HDFC", profile.bankName)
        assertEquals("1234567890", profile.accountNo)
        assertEquals("HDFC0000123", profile.ifsc)
        assertEquals("Andheri", profile.branchName)
    }
}
