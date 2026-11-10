package com.mavo.app.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavo.app.data.BusinessProfile
import com.mavo.app.data.Utils
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.screens.mobile.MobilePair
import com.mavo.app.ui.screens.tablet.TabletPair
import com.mavo.app.ui.theme.AppColors
import com.mavo.app.ui.theme.parseGstinInput
import java.io.Serializable

/**
 * State shared by every setup step — one object, so stepping Back never drops
 * what the user already typed.
 * ponytail: saved as a serialized blob instead of a field-by-field Saver; upgrade
 * path to a real Saver (or a ViewModel-scoped holder) if the draft outgrows the wizard.
 */
data class SetupDraft(
    val businessName: String = "",
    val ownerName: String = "",
    val phone: String = "",
    val altPhone: String = "",
    val email: String = "",
    val address: String = "",
    val businessType: String = "",
    val sellingType: String = "",
    val pin: String = "",
    val city: String = "",
    val state: String = "",
    val stateCode: String = "",
    val gstEnabled: Boolean = false,
    val gstin: String = "",
    val pan: String = "",
    val bankName: String = "",
    val accountNo: String = "",
    val ifsc: String = "",
    val bankBranch: String = ""
) : Serializable {
    fun toProfile() = BusinessProfile(
        businessName = businessName,
        ownerName = ownerName,
        address = address,
        city = city,
        state = state,
        pin = pin,
        phone = phone,
        altPhone = altPhone,
        email = email,
        gstin = gstin,
        pan = pan,
        stateCode = stateCode,
        businessType = businessType,
        sellingType = sellingType,
        bankName = bankName,
        accountNo = accountNo,
        ifsc = ifsc,
        branchName = bankBranch
    )
}

val DraftSaver = listSaver<SetupDraft, Any>(
    save = { listOf(it) },
    restore = { it[0] as SetupDraft }
)

typealias DraftUpdate = (transform: (SetupDraft) -> SetupDraft) -> Unit

private val BUSINESS_TYPES = listOf("Manufacturer", "Wholesaler", "Retailer")
private val SELLING_TYPES = listOf("Products", "Services")

/** Row of fields on tablets (side-by-side), stacked on phones vertically. */
@Composable
private fun AdaptivePair(
    isTablet: Boolean,
    first: @Composable () -> Unit,
    second: @Composable () -> Unit
) {
    if (isTablet) {
        TabletPair(first = first, second = second)
    } else {
        MobilePair(first = first, second = second)
    }
}

@Composable
private fun ChoiceRow(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.labelText
        )
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = selected == option,
                    onClick = { onSelect(option) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    label = { Text(option, fontSize = 13.sp) }
                )
            }
        }
    }
}

@Composable
private fun SetupCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content
    )
}

private fun stepHint(missing: List<RequiredBusinessField>): String? =
    if (missing.isEmpty()) null else "Required to continue: ${missing.joinToString(", ") { it.label }}"

@Composable
fun SetupBasicScreen(
    draft: SetupDraft,
    updateDraft: DraftUpdate,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val missing = missingFieldsFor(SetupStep.Basic, draft.businessName, draft.address, draft.pin)
    var showAltPhone by rememberSaveable { mutableStateOf(draft.altPhone.isNotBlank()) }
    var showEmail by rememberSaveable { mutableStateOf(draft.email.isNotBlank()) }

    WizardScaffold(
        title = "Basic details",
        subtitle = "Business Profile Setup",
        stepLabel = "Step 1 of 3",
        stepProgress = 1,
        onBack = onBack,
        onNext = onNext,
        nextEnabled = missing.isEmpty(),
        hint = stepHint(missing)
    ) {
        SetupCard {
            RetailTextField(
                value = draft.businessName,
                onValueChange = { updateDraft { d -> d.copy(businessName = it) } },
                label = "Business Name *",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.testTag("setup_business_name")
            )
            RetailTextField(
                value = draft.ownerName,
                onValueChange = { updateDraft { d -> d.copy(ownerName = it) } },
                label = "Owner / Signatory Name",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            RetailTextField(
                value = draft.phone,
                onValueChange = { input ->
                    updateDraft { d -> d.copy(phone = input.filter(Char::isDigit).take(15)) }
                },
                label = "Phone Number",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
            )

            if (showAltPhone || draft.altPhone.isNotBlank()) {
                RetailTextField(
                    value = draft.altPhone,
                    onValueChange = { input ->
                        updateDraft { d -> d.copy(altPhone = input.filter(Char::isDigit).take(15)) }
                    },
                    label = "Alternate Phone Number",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                    trailingIcon = {
                        IconButton(onClick = {
                            showAltPhone = false
                            updateDraft { d -> d.copy(altPhone = "") }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Remove Alternate Phone",
                                tint = AppColors.textSecondary
                            )
                        }
                    }
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAltPhone = true }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = AppColors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Add Alternate Phone Number",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.primary
                    )
                }
            }

            if (showEmail || draft.email.isNotBlank()) {
                RetailTextField(
                    value = draft.email,
                    onValueChange = { updateDraft { d -> d.copy(email = it) } },
                    label = "Email Address",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                    trailingIcon = {
                        IconButton(onClick = {
                            showEmail = false
                            updateDraft { d -> d.copy(email = "") }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Remove Email",
                                tint = AppColors.textSecondary
                            )
                        }
                    }
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEmail = true }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = AppColors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Add Email Address",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.primary
                    )
                }
            }

            HorizontalDivider(color = AppColors.divider)
            ChoiceRow(
                label = "Type of Business",
                options = BUSINESS_TYPES,
                selected = draft.businessType,
                onSelect = { updateDraft { d -> d.copy(businessType = it) } }
            )
            ChoiceRow(
                label = "What do you sell?",
                options = SELLING_TYPES,
                selected = draft.sellingType,
                onSelect = { updateDraft { d -> d.copy(sellingType = it) } }
            )
        }
    }
}

@Composable
fun SetupLocationScreen(
    draft: SetupDraft,
    updateDraft: DraftUpdate,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val isTablet = LocalConfiguration.current.screenWidthDp >= 600
    var dropdownExpanded by remember { mutableStateOf(false) }
    var showLocationPopup by rememberSaveable { mutableStateOf(draft.pin.isBlank() && draft.city.isBlank() && draft.address.isBlank() && draft.state.isBlank()) }
    val missing = missingFieldsFor(SetupStep.Location, draft.businessName, draft.address, draft.pin)

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            coroutineScope.launch {
                val detected = detectGstStateInfoFromLocation(context)
                if (detected != null) {
                    updateDraft { d ->
                        d.copy(
                            pin = detected.pin.ifBlank { d.pin },
                            city = detected.city.ifBlank { d.city },
                            state = detected.state.ifBlank { d.state },
                            stateCode = detected.stateCode.ifBlank { d.stateCode }
                        )
                    }
                }
            }
        }
    }

    PinAutoFillEffect(
        pin = draft.pin,
        debounceMs = 300L,
        onResolved = { result ->
            updateDraft { d -> d.copy(city = result.city, state = result.state, stateCode = result.stateCode) }
        }
    )

    if (showLocationPopup) {
        AlertDialog(
            onDismissRequest = { showLocationPopup = false },
            shape = RoundedCornerShape(12.dp),
            containerColor = AppColors.cardBg,
            title = { Text("Location Access", fontWeight = FontWeight.Bold, color = AppColors.textPrimary) },
            text = { Text("Allow location access to collect your real location, or enter manually?", color = AppColors.textSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showLocationPopup = false
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)
                ) {
                    Text("Allow Location")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showLocationPopup = false
                }) {
                    Text("Enter Manually", color = AppColors.primary)
                }
            }
        )
    }

    val stateSuggestions = remember(draft.state) {
        if (draft.state.isBlank() || Utils.INDIAN_STATES.any { it.first.equals(draft.state, ignoreCase = true) }) {
            emptyList()
        } else {
            Utils.INDIAN_STATES.filter { it.first.contains(draft.state, ignoreCase = true) }.take(5)
        }
    }

    WizardScaffold(
        title = "Where you are",
        subtitle = "Business Profile Setup",
        stepLabel = "Step 2 of 3",
        stepProgress = 2,
        onBack = onBack,
        onNext = onNext,
        nextEnabled = missing.isEmpty(),
        hint = stepHint(missing)
    ) {
        SetupCard {
            RetailTextField(
                value = draft.address,
                onValueChange = { updateDraft { d -> d.copy(address = it) } },
                label = "Address (Street / Area) *",
                singleLine = false,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            AdaptivePair(
                isTablet = isTablet,
                first = {
                    RetailTextField(
                        value = draft.pin,
                        onValueChange = { input ->
                            val clean = input.filter { it.isDigit() }
                            if (clean.length <= 6) updateDraft { d -> d.copy(pin = clean) }
                        },
                        label = "PIN Code (6-digit) *",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                    )
                },
                second = {
                    RetailTextField(
                        value = draft.city,
                        onValueChange = { updateDraft { d -> d.copy(city = it) } },
                        label = "City / District",
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                }
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                RetailTextField(
                    value = draft.state,
                    onValueChange = { input ->
                        updateDraft { d ->
                            val matched = resolveIndianStateInfo(input)
                            d.copy(state = input, stateCode = matched?.second ?: "")
                        }
                    },
                    label = "State",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = if (draft.gstEnabled) ImeAction.Next else ImeAction.Done),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Browse States",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { dropdownExpanded = true }
                        )
                    }
                )
                if (stateSuggestions.isNotEmpty()) {
                    DropdownMenu(
                        expanded = true,
                        onDismissRequest = {},
                        properties = androidx.compose.ui.window.PopupProperties(focusable = false),
                        modifier = Modifier.fillMaxWidth(0.92f)
                    ) {
                        stateSuggestions.forEach { suggestion ->
                            DropdownMenuItem(
                                text = { Text(suggestion.first, color = MaterialTheme.colorScheme.onSurface) },
                                onClick = {
                                    updateDraft { d -> d.copy(state = suggestion.first, stateCode = suggestion.second) }
                                    focusManager.clearFocus()
                                }
                            )
                        }
                    }
                }
                StateDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    onStateSelected = { pair ->
                        updateDraft { d -> d.copy(state = pair.first, stateCode = pair.second) }
                        dropdownExpanded = false
                        focusManager.clearFocus()
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Registered under GST",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.textPrimary
                    )
                    Text(
                        text = "Adds the GST state code, GSTIN and PAN fields to the next step.",
                        fontSize = 12.sp,
                        color = AppColors.textSecondary
                    )
                }
                Switch(
                    checked = draft.gstEnabled,
                    onCheckedChange = { checked -> updateDraft { d -> d.copy(gstEnabled = checked) } },
                    colors = SwitchDefaults.colors(checkedTrackColor = AppColors.primary)
                )
            }

            if (draft.gstEnabled) {
                RetailTextField(
                    value = draft.stateCode,
                    onValueChange = {},
                    label = "GST State Code",
                    readOnly = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
            }
        }
    }
}

@Composable
fun SetupTaxBankScreen(
    viewModel: AppViewModel,
    draft: SetupDraft,
    updateDraft: DraftUpdate,
    onBack: () -> Unit,
    onFinish: (loadSampleData: Boolean) -> Unit
) {
    val isTablet = LocalConfiguration.current.screenWidthDp >= 600
    var isGstinLoading by remember { mutableStateOf(false) }
    var gstinValid by remember { mutableStateOf<Boolean?>(null) }
    var isIfscLoading by remember { mutableStateOf(false) }
    var ifscMessage by remember { mutableStateOf("") }
    var askForSampleData by remember { mutableStateOf(false) }

    var showAccountNo by rememberSaveable { mutableStateOf(draft.accountNo.isNotBlank()) }
    var showIfsc by rememberSaveable { mutableStateOf(draft.ifsc.isNotBlank()) }
    var showBankBranch by rememberSaveable { mutableStateOf(draft.bankBranch.isNotBlank()) }
    var showPan by rememberSaveable { mutableStateOf(draft.pan.isNotBlank()) }

    WizardScaffold(
        title = "Tax & bank",
        subtitle = "Business Profile Setup",
        stepLabel = "Step 3 of 3",
        stepProgress = 3,
        onBack = onBack,
        onNext = { askForSampleData = true },
        nextLabel = "Finish Setup",
        nextEnabled = true
    ) {
        // Bank Details first
        SetupCard {
            Text(
                text = "Bank Details for Invoice Payments",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.primary
            )

            // Bank Name first (open text field)
            RetailTextField(
                value = draft.bankName,
                onValueChange = { updateDraft { d -> d.copy(bankName = it) } },
                label = "Bank Name",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            // Optional Account Number
            if (showAccountNo || draft.accountNo.isNotBlank()) {
                RetailTextField(
                    value = draft.accountNo,
                    onValueChange = { input -> updateDraft { d -> d.copy(accountNo = input.filter(Char::isDigit)) } },
                    label = "Account Number",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    trailingIcon = {
                        IconButton(onClick = {
                            showAccountNo = false
                            updateDraft { d -> d.copy(accountNo = "") }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Remove Account Number",
                                tint = AppColors.textSecondary
                            )
                        }
                    }
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAccountNo = true }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = AppColors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Add Account Number",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.primary
                    )
                }
            }

            // Optional IFSC Code
            if (showIfsc || draft.ifsc.isNotBlank()) {
                RetailTextField(
                    value = draft.ifsc,
                    onValueChange = { input ->
                        val ifsc = normalizeIfsc(input)
                        if (ifsc.length <= 11) {
                            updateDraft { d -> d.copy(ifsc = ifsc) }
                            ifscMessage = ""
                        }
                    },
                    label = "IFSC Code",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isIfscLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            IconButton(onClick = {
                                showIfsc = false
                                updateDraft { d -> d.copy(ifsc = "", bankBranch = "") }
                                ifscMessage = ""
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Remove IFSC",
                                    tint = AppColors.textSecondary
                                )
                            }
                        }
                    }
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showIfsc = true }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = AppColors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Add IFSC Code",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.primary
                    )
                }
            }

            IfscVerifyEffect(
                viewModel = viewModel,
                ifsc = draft.ifsc,
                onStart = { isIfscLoading = true },
                onFinish = { isIfscLoading = false },
                onResolved = { bank, branch, message ->
                    updateDraft { d -> d.copy(bankName = bank, bankBranch = branch) }
                    ifscMessage = message
                }
            )

            if (ifscMessage.isNotBlank()) {
                Text(text = ifscMessage, color = AppColors.primary, fontSize = 12.sp)
            }

            // Optional Bank Branch
            if (showBankBranch || draft.bankBranch.isNotBlank()) {
                RetailTextField(
                    value = draft.bankBranch,
                    onValueChange = { updateDraft { d -> d.copy(bankBranch = it) } },
                    label = "Bank Branch",
                    keyboardOptions = KeyboardOptions(imeAction = if (draft.gstEnabled) ImeAction.Next else ImeAction.Done),
                    trailingIcon = {
                        IconButton(onClick = {
                            showBankBranch = false
                            updateDraft { d -> d.copy(bankBranch = "") }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Remove Bank Branch",
                                tint = AppColors.textSecondary
                            )
                        }
                    }
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showBankBranch = true }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = AppColors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Add Bank Branch",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.primary
                    )
                }
            }
        }

        // Tax Details second (if GST enabled)
        if (draft.gstEnabled) {
            SetupCard {
                Text(
                    text = "Tax Details (GST)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.primary
                )

                // GSTIN remains open
                RetailTextField(
                    value = draft.gstin,
                    onValueChange = { input ->
                        val result = parseGstinInput(input, draft.pan, draft.state, draft.stateCode)
                        updateDraft { d ->
                            var next = d.copy(gstin = result.gstin)
                            if (result.valid == true) {
                                next = next.copy(pan = result.pan)
                                result.stateName?.let {
                                    next = next.copy(state = it, stateCode = result.stateCode)
                                }
                            }
                            next
                        }
                        gstinValid = result.valid
                        if (result.gstin.length == 15) {
                            isGstinLoading = true
                            viewModel.fetchGstinDetails(result.gstin) { trade, legal ->
                                isGstinLoading = false
                                val detectedName = trade ?: legal
                                if (!detectedName.isNullOrBlank()) {
                                    updateDraft { d -> if (d.businessName.isBlank()) d.copy(businessName = detectedName) else d }
                                }
                            }
                        } else {
                            isGstinLoading = false
                        }
                    },
                    label = "GSTIN *",
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    trailingIcon = {
                        if (isGstinLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                )

                // Optional PAN
                if (showPan || draft.pan.isNotBlank()) {
                    RetailTextField(
                        value = draft.pan,
                        onValueChange = { input -> updateDraft { d -> d.copy(pan = input.uppercase()) } },
                        label = "PAN",
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        trailingIcon = {
                            IconButton(onClick = {
                                showPan = false
                                updateDraft { d -> d.copy(pan = "") }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Remove PAN",
                                    tint = AppColors.textSecondary
                                )
                            }
                        }
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showPan = true }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = AppColors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Add PAN",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = AppColors.primary
                        )
                    }
                }
            }
        }
    }

    if (askForSampleData) {
        AlertDialog(
            onDismissRequest = { askForSampleData = false },
            containerColor = AppColors.cardBg,
            titleContentColor = AppColors.textPrimary,
            textContentColor = AppColors.textSecondary,
            title = {
                Text("Explore with Sample Data?", fontWeight = FontWeight.Bold, color = AppColors.textPrimary)
            },
            text = {
                Text(
                    "Would you like to load sample products, parties, and vouchers to instantly see how the charts, outstanding balances, and GST summary ledger reports operate?",
                    color = AppColors.textSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { onFinish(true) },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)
                ) {
                    Text("Yes, Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { onFinish(false) }) {
                    Text("No, Start Clean", color = AppColors.primary)
                }
            }
        )
    }
}
