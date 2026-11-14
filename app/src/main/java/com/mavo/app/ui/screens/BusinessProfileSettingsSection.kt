package com.mavo.app.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mavo.app.data.BusinessProfile
import com.mavo.app.data.DEFAULT_TERMS_AND_CONDITIONS
import com.mavo.app.data.Utils
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.components.CircleIconButton
import com.mavo.app.ui.components.ZbField
import com.mavo.app.ui.theme.AppColors
import com.mavo.app.ui.theme.TextDark
import com.mavo.app.utils.copyUriToInternalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val BUSINESS_TYPES = listOf("Manufacturer", "Wholesaler", "Retailer")
private val SELLING_TYPES = listOf("Products", "Services")

@Composable
private fun ChoiceRow(
    label: String,
    options: List<String>,
    selected: String,
    enabled: Boolean,
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
                    onClick = { if (enabled) onSelect(option) },
                    enabled = enabled,
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    label = { Text(option, fontSize = 13.sp) }
                )
            }
        }
    }
}

@Composable
private fun SetupCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BusinessProfileSettingsSection(
    viewModel: AppViewModel,
    profile: BusinessProfile?,
    isDesktop: Boolean,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val profileScope = rememberCoroutineScope()
    val formScroll = rememberScrollState()

    val resolvedProfile = profile ?: BusinessProfile(
        businessName = "",
        ownerName = "",
        address = "",
        city = "",
        state = "West Bengal",
        stateCode = "19",
        pin = "",
        phone = "",
        email = "",
        gstin = "",
        pan = "",
        bankName = "",
        accountNo = "",
        ifsc = "",
        logoPath = null,
        signaturePath = null
    )

    var isEditing by remember { mutableStateOf(false) }

    var businessName by remember { mutableStateOf(resolvedProfile.businessName) }
    var ownerName by remember { mutableStateOf(resolvedProfile.ownerName) }
    var phone by remember { mutableStateOf(resolvedProfile.phone) }
    var altPhone by remember { mutableStateOf(resolvedProfile.altPhone) }
    var showAltPhone by remember { mutableStateOf(resolvedProfile.altPhone.isNotBlank()) }
    var email by remember { mutableStateOf(resolvedProfile.email) }
    var showEmail by remember { mutableStateOf(resolvedProfile.email.isNotBlank()) }
    var businessType by remember { mutableStateOf(resolvedProfile.businessType) }
    var sellingType by remember { mutableStateOf(resolvedProfile.sellingType) }

    var address by remember { mutableStateOf(resolvedProfile.address) }
    var pinCode by remember { mutableStateOf(resolvedProfile.pin) }
    var city by remember { mutableStateOf(resolvedProfile.city) }
    var selectedStateInfo by remember {
        mutableStateOf(
            Utils.INDIAN_STATES.find { it.first == resolvedProfile.state }
                ?: Utils.INDIAN_STATES[18]
        )
    }
    var stateDropdownExpanded by remember { mutableStateOf(false) }
    var logoPath by remember { mutableStateOf(resolvedProfile.logoPath) }
    var uploadedSignaturePath by remember { mutableStateOf(resolvedProfile.signaturePath) }
    var termsAndConditions by remember {
        mutableStateOf(resolvedProfile.termsAndConditions.ifBlank { DEFAULT_TERMS_AND_CONDITIONS })
    }
    var locationDetectionMessage by remember { mutableStateOf("") }
    var isLocationDetecting by remember { mutableStateOf(false) }
    var locationPopup by remember { mutableStateOf(false) }
    var showUnsavedChangesDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = isEditing) {
        showUnsavedChangesDialog = true
    }

    if (showUnsavedChangesDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedChangesDialog = false },
            shape = RoundedCornerShape(12.dp),
            containerColor = AppColors.cardBg,
            title = { Text("Unsaved Changes", fontWeight = FontWeight.Bold, color = AppColors.textPrimary) },
            text = { Text("You have unsaved edits. Do you want to save or discard your changes?", color = AppColors.textSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showUnsavedChangesDialog = false
                        if (businessName.isBlank()) {
                            Toast.makeText(context, "Business Name is required", Toast.LENGTH_SHORT).show()
                        } else if (address.isBlank()) {
                            Toast.makeText(context, "Address is required", Toast.LENGTH_SHORT).show()
                        } else if (pinCode.isBlank()) {
                            Toast.makeText(context, "PIN Code is required", Toast.LENGTH_SHORT).show()
                        } else {
                            val nextProfile = resolvedProfile.copy(
                                businessName = businessName,
                                ownerName = ownerName,
                                address = address,
                                city = city,
                                pin = pinCode,
                                state = selectedStateInfo.first,
                                stateCode = selectedStateInfo.second,
                                phone = phone,
                                altPhone = altPhone,
                                email = email,
                                businessType = businessType,
                                sellingType = sellingType,
                                logoPath = logoPath,
                                signaturePath = uploadedSignaturePath,
                                termsAndConditions = termsAndConditions.ifBlank { DEFAULT_TERMS_AND_CONDITIONS }
                            )
                            viewModel.updateProfile(nextProfile) {
                                isEditing = false
                                Toast.makeText(context, "Business Profile successfully saved", Toast.LENGTH_SHORT).show()
                                onBackToMenu()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showUnsavedChangesDialog = false
                        isEditing = false
                        onBackToMenu()
                    }
                ) {
                    Text("Discard", color = AppColors.error)
                }
            }
        )
    }

    val businessNameFocusRequester = remember { FocusRequester() }

    suspend fun runLocationDetection() {
        isLocationDetecting = true
        val detectedState = detectGstStateInfoFromLocation(context)
        if (detectedState != null) {
            selectedStateInfo = detectedState.state to detectedState.stateCode
            locationDetectionMessage = "GST state code auto-detected via location."
        } else {
            locationDetectionMessage = ""
        }
        isLocationDetecting = false
    }

    val singleLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            profileScope.launch { runLocationDetection() }
        }
    }

    PinAutoFillEffect(
        pin = pinCode,
        onInvalid = {},
        onStart = {},
        onResolved = { result ->
            city = result.city
            resolveIndianStateInfo(result.state)?.let { selectedStateInfo = it }
        },
        onFailed = {}
    )

    if (locationPopup) {
        AlertDialog(
            onDismissRequest = { locationPopup = false },
            shape = RoundedCornerShape(12.dp),
            containerColor = AppColors.cardBg,
            title = { Text("Location Access", fontWeight = FontWeight.Bold, color = AppColors.textPrimary) },
            text = { Text("Allow location access to collect your real location, or enter manually?", color = AppColors.textSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        locationPopup = false
                        singleLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)
                ) {
                    Text("Allow Location")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    locationPopup = false
                }) {
                    Text("Enter Manually", color = AppColors.primary)
                }
            }
        )
    }

    val signatureUploadLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null && isEditing) {
            profileScope.launch {
                val ext = when {
                    context.contentResolver.getType(uri)?.contains("pdf") == true -> ".pdf"
                    else -> ".jpg"
                }
                val path = withContext(Dispatchers.IO) {
                    copyUriToInternalStorage(
                        context,
                        uri,
                        "business_signature_${System.currentTimeMillis()}$ext"
                    )
                }
                path?.let {
                    uploadedSignaturePath = it
                    Toast.makeText(context, "Signature uploaded", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val stateSuggestions = remember(selectedStateInfo.first) {
        if (selectedStateInfo.first.isBlank() || Utils.INDIAN_STATES.any { it.first.equals(selectedStateInfo.first, ignoreCase = true) }) {
            emptyList()
        } else {
            Utils.INDIAN_STATES.filter { it.first.contains(selectedStateInfo.first, ignoreCase = true) }.take(5)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.screenBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isDesktop) {
                CircleIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    onClick = {
                        if (isEditing) {
                            showUnsavedChangesDialog = true
                        } else {
                            onBackToMenu()
                        }
                    }
                )
            } else {
                Spacer(modifier = Modifier.width(44.dp))
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Master data",
                fontSize = 16.sp,
                color = AppColors.textSecondary
            )
            Spacer(modifier = Modifier.weight(1f))
            CircleIconButton(
                icon = Icons.Outlined.Edit,
                contentDescription = "Edit",
                onClick = {
                    isEditing = true
                    profileScope.launch {
                        formScroll.animateScrollTo(0)
                        businessNameFocusRequester.requestFocus()
                        keyboardController?.show()
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(formScroll)
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(0.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AppColors.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Business,
                            contentDescription = null,
                            tint = AppColors.textOnPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = resolvedProfile.businessName.ifBlank { "Business profile" },
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = listOfNotNull(
                                resolvedProfile.city.ifBlank { null },
                                resolvedProfile.state.ifBlank { null }
                            ).joinToString(", ").ifBlank {
                                resolvedProfile.address.ifBlank { null } ?: "Address not set"
                            },
                            fontSize = 15.sp,
                            color = AppColors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = resolvedProfile.ownerName.ifBlank { "Proprietor not set" },
                            fontSize = 15.sp,
                            color = AppColors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = isEditing, onClick = {})
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Card 1: Basic Details
                    SetupCard {
                        Text(
                            text = "Basic Details",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.primary
                        )
                        ZbField(
                            value = businessName,
                            onValueChange = { if (isEditing) businessName = it },
                            label = "Business Name *",
                            readOnly = !isEditing,
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(businessNameFocusRequester)
                                .testTag("edit_bname"),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        ZbField(
                            value = ownerName,
                            onValueChange = { if (isEditing) ownerName = it },
                            label = "Owner / Signatory Name",
                            readOnly = !isEditing,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        ZbField(
                            value = phone,
                            onValueChange = { input ->
                                if (isEditing) {
                                    phone = input.filter(Char::isDigit).take(15)
                                }
                            },
                            label = "Phone Number",
                            readOnly = !isEditing,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
                        )

                        if (showAltPhone || altPhone.isNotBlank()) {
                            ZbField(
                                value = altPhone,
                                onValueChange = { input ->
                                    if (isEditing) {
                                        altPhone = input.filter(Char::isDigit).take(15)
                                    }
                                },
                                label = "Alternate Phone Number",
                                readOnly = !isEditing,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                                trailingIcon = {
                                    if (isEditing) {
                                        IconButton(onClick = {
                                            showAltPhone = false
                                            altPhone = ""
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Remove,
                                                contentDescription = "Remove Alternate Phone",
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
                                    .clickable {
                                        isEditing = true
                                        showAltPhone = true
                                    }
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

                        if (showEmail || email.isNotBlank()) {
                            ZbField(
                                value = email,
                                onValueChange = { if (isEditing) email = it },
                                label = "Email Address",
                                readOnly = !isEditing,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                                trailingIcon = {
                                    if (isEditing) {
                                        IconButton(onClick = {
                                            showEmail = false
                                            email = ""
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Remove,
                                                contentDescription = "Remove Email",
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
                                    .clickable {
                                        isEditing = true
                                        showEmail = true
                                    }
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
                            selected = businessType,
                            enabled = isEditing,
                            onSelect = { businessType = it }
                        )
                        ChoiceRow(
                            label = "What do you sell?",
                            options = SELLING_TYPES,
                            selected = sellingType,
                            enabled = isEditing,
                            onSelect = { sellingType = it }
                        )
                    }

                    // Card 2: Location
                    SetupCard {
                        Text(
                            text = "Location & State",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.primary
                        )
                        ZbField(
                            value = address,
                            onValueChange = { if (isEditing) address = it },
                            label = "Address (Street / Area) *",
                            readOnly = !isEditing,
                            singleLine = false,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ZbField(
                                value = pinCode,
                                onValueChange = { input ->
                                    if (isEditing) {
                                        val clean = input.filter { it.isDigit() }
                                        if (clean.length <= 6) pinCode = clean
                                    }
                                },
                                label = "PIN Code (6-digit) *",
                                readOnly = !isEditing,
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                            )
                            ZbField(
                                value = city,
                                onValueChange = { if (isEditing) city = it },
                                label = "City / District",
                                readOnly = !isEditing,
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                            )
                        }

                        Box(modifier = Modifier.fillMaxWidth()) {
                            ZbField(
                                value = selectedStateInfo.first,
                                onValueChange = { input ->
                                    if (isEditing) {
                                        val matched = resolveIndianStateInfo(input)
                                        selectedStateInfo = input to (matched?.second ?: selectedStateInfo.second)
                                    }
                                },
                                label = "State",
                                readOnly = !isEditing,
                                singleLine = true,
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Browse States",
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clickable(enabled = isEditing) { stateDropdownExpanded = true }
                                    )
                                }
                            )
                            if (stateSuggestions.isNotEmpty() && isEditing) {
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
                                                selectedStateInfo = suggestion
                                                focusManager.clearFocus()
                                            }
                                        )
                                    }
                                }
                            }
                            StateDropdownMenu(
                                expanded = stateDropdownExpanded,
                                onDismissRequest = { stateDropdownExpanded = false },
                                onStateSelected = { pair ->
                                    if (isEditing) {
                                        selectedStateInfo = pair
                                        stateDropdownExpanded = false
                                        locationDetectionMessage = ""
                                        focusManager.clearFocus()
                                    }
                                }
                            )
                        }

                        if (isEditing) {
                            OutlinedButton(
                                onClick = {
                                    val hasFine = androidx.core.content.ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_FINE_LOCATION
                                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                    val hasCoarse = androidx.core.content.ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                    if (hasFine || hasCoarse) {
                                        profileScope.launch { runLocationDetection() }
                                    } else {
                                        locationPopup = true
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (isLocationDetecting) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                } else {
                                    Text("Detect via Location", fontSize = 12.sp)
                                }
                            }
                        }

                        if (locationDetectionMessage.isNotBlank()) {
                            Text(
                                text = locationDetectionMessage,
                                color = AppColors.textSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Branding & Invoice images section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text("BRANDING & INVOICE IMAGES", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AppColors.textSecondary)
                    }

                    BusinessImageUploadSection(
                        title = "Business Logo",
                        subtitle = "Appears on invoice header. PNG or JPG recommended.",
                        currentPath = logoPath,
                        onImageSelected = { uri ->
                            if (isEditing) {
                                profileScope.launch {
                                    val path = withContext(Dispatchers.IO) {
                                        copyUriToInternalStorage(
                                            context,
                                            uri,
                                            "business_logo_${System.currentTimeMillis()}.jpg"
                                        )
                                    }
                                    path?.let { logoPath = it }
                                }
                            }
                        }
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(0.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "Authorized Signature",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = TextDark
                            )
                            Text(
                                "Upload a photo of your signature",
                                fontSize = 11.sp,
                                color = AppColors.textSecondary,
                                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                            )
                            val signatureFile = uploadedSignaturePath?.let { java.io.File(it) }
                            if (signatureFile != null && signatureFile.exists() && !signatureFile.extension.equals("pdf", ignoreCase = true)) {
                                AsyncImage(
                                    model = signatureFile,
                                    contentDescription = "Signature preview",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AppColors.screenBg),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(Modifier.height(12.dp))
                                if (isEditing) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = {
                                                signatureUploadLauncher.launch(arrayOf("image/*", "application/pdf"))
                                            },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Change", color = TextDark, fontSize = 12.sp)
                                        }
                                        OutlinedButton(
                                            onClick = { uploadedSignaturePath = null },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Remove", color = AppColors.error, fontSize = 12.sp)
                                        }
                                    }
                                }
                            } else if (signatureFile != null && signatureFile.exists()) {
                                Text("PDF signature uploaded: ${signatureFile.name}", color = TextDark, fontSize = 12.sp)
                                Spacer(Modifier.height(8.dp))
                                if (isEditing) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = {
                                                signatureUploadLauncher.launch(arrayOf("image/*", "application/pdf"))
                                            },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Change", color = TextDark, fontSize = 12.sp)
                                        }
                                        OutlinedButton(
                                            onClick = { uploadedSignaturePath = null },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Remove", color = AppColors.error, fontSize = 12.sp)
                                        }
                                    }
                                }
                            } else {
                                if (isEditing) {
                                    Button(
                                        onClick = {
                                            signatureUploadLauncher.launch(arrayOf("image/*", "application/pdf"))
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)
                                    ) {
                                        Text("Upload Signature", color = AppColors.textOnPrimary)
                                    }
                                } else {
                                    Text("No signature uploaded", fontSize = 12.sp, color = AppColors.textSecondary)
                                }
                            }
                        }
                    }

                    // Invoice settings section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text("INVOICE SETTINGS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AppColors.textSecondary)
                    }
                    ZbField(
                        value = termsAndConditions,
                        onValueChange = { if (isEditing) termsAndConditions = it },
                        label = "Terms & Conditions / Declaration",
                        placeholder = "Default terms printed on every invoice",
                        readOnly = !isEditing,
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Save Card Button
            if (isEditing) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            onClick = {
                                if (businessName.isBlank()) {
                                    Toast.makeText(context, "Business Name is required", Toast.LENGTH_SHORT).show()
                                } else if (address.isBlank()) {
                                    Toast.makeText(context, "Address is required", Toast.LENGTH_SHORT).show()
                                } else if (pinCode.isBlank()) {
                                    Toast.makeText(context, "PIN Code is required", Toast.LENGTH_SHORT).show()
                                } else {
                                    val nextProfile = resolvedProfile.copy(
                                        businessName = businessName,
                                        ownerName = ownerName,
                                        address = address,
                                        city = city,
                                        pin = pinCode,
                                        state = selectedStateInfo.first,
                                        stateCode = selectedStateInfo.second,
                                        phone = phone,
                                        altPhone = altPhone,
                                        email = email,
                                        businessType = businessType,
                                        sellingType = sellingType,
                                        logoPath = logoPath,
                                        signaturePath = uploadedSignaturePath,
                                        termsAndConditions = termsAndConditions.ifBlank { DEFAULT_TERMS_AND_CONDITIONS }
                                    )
                                    viewModel.updateProfile(nextProfile) {
                                        isEditing = false
                                        Toast.makeText(context, "Business Profile successfully updated", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        )
                        .testTag("save_profile_button"),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(0.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.primary)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Save",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.textOnPrimary
                        )
                    }
                }
            }
        }
    }
}
