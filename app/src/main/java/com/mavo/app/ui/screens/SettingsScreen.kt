package com.mavo.app.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Build
import android.text.format.DateUtils
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Tune
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavo.app.R
import com.mavo.app.data.*
import com.mavo.app.services.ExportStorageManager
import com.mavo.app.services.ExportTarget
import com.mavo.app.services.CsvTransferManager
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.components.CircleIconButton
import com.mavo.app.ui.components.EmptyState
import com.mavo.app.ui.components.PrimaryButton
import com.mavo.app.ui.components.ScreenBorder
import com.mavo.app.ui.components.SecondaryButton
import com.mavo.app.ui.components.SectionLabel
import com.mavo.app.ui.components.WizardHeader
import com.mavo.app.ui.components.WizardTitle
import com.mavo.app.ui.components.ZbCard
import com.mavo.app.ui.components.ZbField
import com.mavo.app.ui.theme.AppColors
import com.mavo.app.ui.theme.GstinValidationFeedback
import com.mavo.app.ui.theme.LocalAppTheme
import com.mavo.app.ui.theme.ThemeViewModel
import com.mavo.app.ui.theme.parseGstinInput
import com.mavo.app.ui.theme.*
import com.mavo.app.ui.animation.pressScale
import com.mavo.app.ui.animation.premiumClickable
import com.mavo.app.utils.copyUriToInternalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.mavo.app.BuildConfig

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: AppViewModel,
    themeViewModel: ThemeViewModel,
    isDesktop: Boolean = false,
    navigateToProducts: () -> Unit,
    navigateToLedgerBooks: () -> Unit,
    navigateToParties: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val origProfile by viewModel.profile.collectAsState()
    val currentTheme by themeViewModel.currentTheme.collectAsState()
    val vouchersForExport by viewModel.vouchers.collectAsState(initial = emptyList())
    val partiesForExport by viewModel.parties.collectAsState(initial = emptyList())
    val productsForExport by viewModel.products.collectAsState(initial = emptyList())
    val ledgerForExport by viewModel.ledgerEntries.collectAsState(initial = emptyList())
    val importCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            kotlinx.coroutines.CoroutineScope(Dispatchers.Main).launch {
                val summary = withContext(Dispatchers.IO) {
                    CsvTransferManager.importCsv(
                        context = context,
                        uri = uri,
                        financialYearCode = viewModel.financialYear.value
                    )
                }
                Toast.makeText(
                    context,
                    "Imported ${summary.importedCount} records, ${summary.skippedCount} skipped",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    var activeSubMode by remember { mutableStateOf(if (isDesktop) "BUSINESS" else "MENU") }
    var changelogData by remember { mutableStateOf<ChangelogData?>(null) }
    var showChangelogDialog by remember { mutableStateOf(false) }

    LaunchedEffect(context) {
        changelogData = ChangelogLoader.load(context)
    }

    val exportCsv: () -> Unit = {
        try {
            CsvTransferManager.exportAll(
                context = context,
                vouchers = vouchersForExport,
                parties = partiesForExport,
                products = productsForExport,
                ledgerEntries = ledgerForExport,
                partyLookup = partiesForExport.associate { it.id to it.name },
                financialYearLabel = viewModel.financialYear.value
            )
            Toast.makeText(context, "Exported to Downloads/Mavo/", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    val importCsv: () -> Unit = {
        importCsvLauncher.launch(arrayOf("text/*", "text/csv", "application/csv", "*/*"))
    }

    @Composable
    fun DetailContent() {
        if (activeSubMode == "BUSINESS") {
            BusinessProfileSettingsSection(
                viewModel = viewModel,
                profile = origProfile,
                isDesktop = isDesktop,
                onBackToMenu = { activeSubMode = "MENU" }
            )
            return
        }
        if (activeSubMode == "CUSTOMIZE") {
            ProgressTrackerSettingsScreen(
                onBackToMenu = { activeSubMode = "MENU" }
            )
            return
        }
        if (activeSubMode == "DATA") {
            DataManagementContent(
                viewModel = viewModel,
                exportCsv = exportCsv,
                importCsv = importCsv,
                recordCount = vouchersForExport.size + partiesForExport.size +
                    productsForExport.size + ledgerForExport.size,
                onBack = { activeSubMode = "MENU" },
                onAuditLog = { activeSubMode = "AUDIT" }
            )
            return
        }
        if (activeSubMode == "AUDIT") {
            AuditLogContent(
                viewModel = viewModel,
                onBack = { activeSubMode = "DATA" }
            )
            return
        }
        if (false && activeSubMode == "BUSINESS") {
            // Business Profile Form Editor
            val profile = origProfile ?: BusinessProfile(
                businessName = "", ownerName = "", address = "", city = "", state = "West Bengal", stateCode = "19",
                pin = "", phone = "", email = "", gstin = "", pan = "", bankName = "", accountNo = "", ifsc = "",
                logoPath = null, signaturePath = null
            )

            var bName by remember { mutableStateOf(profile.businessName) }
            var owner by remember { mutableStateOf(profile.ownerName) }
            var address by remember { mutableStateOf(profile.address) }
            var city by remember { mutableStateOf(profile.city) }
            var pinCode by remember { mutableStateOf(profile.pin) }
            var selectedStateInfo by remember { mutableStateOf(Utils.INDIAN_STATES.find { it.first == profile.state } ?: Utils.INDIAN_STATES[18]) }
            var phone by remember { mutableStateOf(profile.phone) }
            var email by remember { mutableStateOf(profile.email) }
            var gstin by remember { mutableStateOf(profile.gstin) }
            var gstinValid by remember { mutableStateOf<Boolean?>(null) }
            var pan by remember { mutableStateOf(profile.pan) }
            var bankName by remember { mutableStateOf(profile.bankName) }
            var accountNo by remember { mutableStateOf(profile.accountNo) }
            var ifsc by remember { mutableStateOf(profile.ifsc) }
            var bankBranch by remember { mutableStateOf(profile.branchName) }
            var isIfscLoading by remember { mutableStateOf(false) }
            var ifscVerifiedMessage by remember { mutableStateOf("") }
            var isPinLoading by remember { mutableStateOf(false) }
            var pinLookupMessage by remember { mutableStateOf("") }
            var pinLookupSuccess by remember { mutableStateOf(false) }
            var stateDropdownExpanded by remember { mutableStateOf(false) }
            var logoPath by remember { mutableStateOf(profile.logoPath) }
            var uploadedSignaturePath by remember { mutableStateOf(profile.signaturePath) }
            var termsAndConditions by remember {
                mutableStateOf(profile.termsAndConditions.ifBlank { DEFAULT_TERMS_AND_CONDITIONS })
            }
            val profileScope = rememberCoroutineScope()
            val formScroll = rememberScrollState()

            LaunchedEffect(pinCode) {
                if (pinCode.length == 6 && pinCode.all { it.isDigit() }) {
                    delay(300)
                    isPinLoading = true
                    val result = fetchPinLookup(pinCode)
                    isPinLoading = false
                    if (result != null) {
                        city = result.city
                        pinLookupMessage = "City: ${result.city}, ${result.state}"
                        pinLookupSuccess = true
                        Utils.INDIAN_STATES.find {
                            it.second == result.stateCode || it.first.equals(result.state, ignoreCase = true)
                        }?.let { selectedStateInfo = it }
                    } else {
                        pinLookupMessage = "City not found — enter manually"
                        pinLookupSuccess = false
                    }
                } else {
                    isPinLoading = false
                    pinLookupMessage = ""
                    pinLookupSuccess = false
                }
            }

            val signatureUploadLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                contract = androidx.activity.result.contract.ActivityResultContracts.OpenDocument()
            ) { uri ->
                if (uri != null) {
                    profileScope.launch {
                        val ext = when {
                            context.contentResolver.getType(uri)?.contains("pdf") == true -> ".pdf"
                            else -> ".jpg"
                        }
                        val path = withContext(kotlinx.coroutines.Dispatchers.IO) {
                            com.mavo.app.utils.copyUriToInternalStorage(
                                context, uri,
                                "business_signature_${System.currentTimeMillis()}$ext"
                            )
                        }
                        path?.let {
                            uploadedSignaturePath = it
                            android.widget.Toast.makeText(context, "Signature uploaded!", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        Scaffold(
            containerColor = AppColors.screenBg,
            topBar = {
                TopAppBar(
                    title = { Text("Business Profile", fontWeight = FontWeight.Bold, color = AppColors.textPrimary) },
                    navigationIcon = {
                        if (!isDesktop) {
                            IconButton(onClick = { activeSubMode = "MENU" }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppColors.textPrimary)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.cardBg)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.screenBg)
                    .verticalScroll(formScroll)
                    .imePadding()
                    .padding(innerPadding)
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RetailTextField(
                    value = bName,
                    onValueChange = { bName = it },
                    label = "Business Name *",
                    modifier = Modifier.fillMaxWidth().testTag("edit_bname")
                )

                RetailTextField(
                    value = owner,
                    onValueChange = { owner = it },
                    label = "Owner / Proprietor *"
                )

                RetailTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = "Business Address *"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RetailTextField(
                        value = pinCode,
                        onValueChange = { pinCode = it.filter(Char::isDigit).take(6) },
                        label = "PIN Code *",
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        trailingIcon = {
                            if (isPinLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            }
                        }
                    )

                    RetailTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = "City *",
                        modifier = Modifier.weight(1f)
                    )
                }

                if (pinLookupMessage.isNotBlank()) {
                    Text(
                        text = pinLookupMessage,
                        color = if (pinLookupSuccess) Color(0xFF2E7D32) else Color.Gray,
                        fontSize = 11.sp
                    )
                }

                // State Selector Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    RetailTextField(
                        value = "${selectedStateInfo.first} (${selectedStateInfo.second})",
                        onValueChange = {},
                        label = "State Code Dropdown *",
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = AppColors.textSecondary,
                                modifier = Modifier.clickable { stateDropdownExpanded = true }
                            )
                        }
                    )
                    DropdownMenu(
                        expanded = stateDropdownExpanded,
                        onDismissRequest = { stateDropdownExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(240.dp)
                            .background(AppColors.cardBg)
                    ) {
                        Utils.INDIAN_STATES.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.first, color = AppColors.textPrimary) },
                                onClick = {
                                    selectedStateInfo = item
                                    stateDropdownExpanded = false
                                },
                                colors = MenuDefaults.itemColors(textColor = AppColors.textPrimary)
                            )
                        }
                    }
                }

                RetailTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Phone Number *",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                RetailTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address *",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Text(
                    "GSTIN Number (Optional)",
                    color = AppColors.labelText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                OutlinedTextField(
                    value = gstin,
                    onValueChange = { value ->
                        val result = parseGstinInput(
                            value, pan, selectedStateInfo.first, selectedStateInfo.second
                        )
                        gstin = result.gstin
                        gstinValid = result.valid
                        if (result.valid == true) {
                            pan = result.pan
                            result.stateName?.let { name ->
                                Utils.INDIAN_STATES.find { it.first == name }
                                    ?.let { selectedStateInfo = it }
                            }
                        }
                    },
                    placeholder = { Text("15-digit GSTIN", color = AppColors.inputPlaceholder) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = com.mavo.app.ui.theme.mavoInputColors(),
                    singleLine = true
                )
                GstinValidationFeedback(
                    gstin, gstinValid, pan, selectedStateInfo.first, selectedStateInfo.second
                )

                RetailTextField(
                    value = pan,
                    onValueChange = { pan = it.uppercase() },
                    label = "PAN Card Number (Optional)"
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text("BANK DETAILS SETUP", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AppColors.textSecondary)

                RetailTextField(
                    value = accountNo,
                    onValueChange = { accountNo = it.filter(Char::isDigit) },
                    label = "Account Number *",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                RetailTextField(
                    value = ifsc,
                    onValueChange = { input ->
                        val uppercaseIn = input.uppercase().replace("\\s".toRegex(), "")
                        if (uppercaseIn.length <= 11) {
                            ifsc = uppercaseIn
                            ifscVerifiedMessage = ""
                            if (uppercaseIn.length == 11) {
                                isIfscLoading = true
                                viewModel.fetchIfscDetails(uppercaseIn) { resolvedBank, resolvedBranch ->
                                    isIfscLoading = false
                                    if (!resolvedBank.isNullOrBlank()) {
                                        bankName = resolvedBank
                                        bankBranch = resolvedBranch.orEmpty()
                                        ifscVerifiedMessage = buildString {
                                            append("Bank verified: ")
                                            append(resolvedBank)
                                            if (!resolvedBranch.isNullOrBlank()) {
                                                append(", ")
                                                append(resolvedBranch)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    label = "IFSC Code *",
                    trailingIcon = {
                        if (isIfscLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        }
                    }
                )

                if (ifscVerifiedMessage.isNotBlank()) {
                    Text(ifscVerifiedMessage, color = Color(0xFF2E7D32), fontSize = 12.sp)
                }

                RetailTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = "Bank Name *"
                )

                RetailTextField(
                    value = bankBranch,
                    onValueChange = { bankBranch = it },
                    label = "Bank Branch"
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text("BRANDING & INVOICE IMAGES", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AppColors.textSecondary)

                BusinessImageUploadSection(
                    title = "Business Logo",
                    subtitle = "Appears on invoice header. PNG or JPG recommended.",
                    currentPath = logoPath,
                    onImageSelected = { uri ->
                        profileScope.launch {
                            val path = withContext(Dispatchers.IO) {
                                copyUriToInternalStorage(
                                    context, uri,
                                    "business_logo_${System.currentTimeMillis()}.jpg"
                                )
                            }
                            path?.let { logoPath = it }
                        }
                    }
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AppColors.cardBg),
                    border = BorderStroke(1.dp, AppColors.border),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "Authorized Signature",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = com.mavo.app.ui.theme.TextDark
                        )
                        Text(
                            "Upload a photo of your signature",
                            fontSize = 11.sp,
                            color = AppColors.textSecondary,
                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                        )
                        val sigFile = uploadedSignaturePath?.let { java.io.File(it) }
                        if (sigFile != null && sigFile.exists() && !sigFile.extension.equals("pdf", ignoreCase = true)) {
                            coil.compose.AsyncImage(
                                model = sigFile,
                                contentDescription = "Signature preview",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, AppColors.border, RoundedCornerShape(8.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Fit
                            )
                            Spacer(Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        signatureUploadLauncher.launch(arrayOf("image/*", "application/pdf"))
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Change", color = com.mavo.app.ui.theme.TextDark, fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = { uploadedSignaturePath = null },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Remove", color = AppColors.error, fontSize = 12.sp)
                                }
                            }
                        } else if (sigFile != null && sigFile.exists()) {
                            Text("PDF signature uploaded: ${sigFile.name}", color = com.mavo.app.ui.theme.TextDark, fontSize = 12.sp)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = {
                                    signatureUploadLauncher.launch(arrayOf("image/*", "application/pdf"))
                                }, modifier = Modifier.weight(1f)) {
                                    Text("Change", color = com.mavo.app.ui.theme.TextDark, fontSize = 12.sp)
                                }
                                OutlinedButton(onClick = { uploadedSignaturePath = null }, modifier = Modifier.weight(1f)) {
                                    Text("Remove", color = AppColors.error, fontSize = 12.sp)
                                }
                            }
                        } else {
                            Button(
                                onClick = {
                                    signatureUploadLauncher.launch(arrayOf("image/*", "application/pdf"))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)
                            ) {
                                Text("Upload Signature", color = AppColors.textOnPrimary)
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                Text("INVOICE SETTINGS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AppColors.textSecondary)
                RetailTextField(
                    value = termsAndConditions,
                    onValueChange = { termsAndConditions = it },
                    label = "Terms & Conditions / Declaration",
                    placeholder = "Enter your default terms and conditions that will appear on every invoice",
                    singleLine = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (bName.isBlank() || owner.isBlank() || address.isBlank() || city.isBlank() || phone.isBlank() || bankName.isBlank() || accountNo.isBlank() || ifsc.isBlank()) {
                            Toast.makeText(context, "Please fill in all required fields accurately", Toast.LENGTH_SHORT).show()
                        } else if (gstin.isNotBlank() && gstin.length != 15) {
                            Toast.makeText(context, "GSTIN must be exactly 15 characters long if provided", Toast.LENGTH_SHORT).show()
                        } else {
                            val nextProfile = profile.copy(
                                businessName = bName, ownerName = owner, address = address, city = city,
                                pin = pinCode,
                                state = selectedStateInfo.first, stateCode = selectedStateInfo.second,
                                phone = phone, email = email, gstin = gstin, pan = pan,
                                bankName = bankName, accountNo = accountNo, ifsc = ifsc, branchName = bankBranch,
                                logoPath = logoPath,
                                signaturePath = uploadedSignaturePath,
                                termsAndConditions = termsAndConditions.ifBlank { DEFAULT_TERMS_AND_CONDITIONS }
                            )
                            viewModel.updateProfile(nextProfile) {
                                Toast.makeText(context, "Business Profile successfully updated!", Toast.LENGTH_SHORT).show()
                                activeSubMode = "MENU"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_business_profile"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                    Text("Save Alterations Settings", fontWeight = FontWeight.Bold)
                }
            }
        }
    } else if (activeSubMode == "LOCK") {
        // App security control
        val sp = context.getSharedPreferences("mavo_pref", Context.MODE_PRIVATE)
        var pinEnabled by remember { mutableStateOf(sp.getBoolean("pin_enabled", false)) }
        var enteredPin by remember { mutableStateOf(sp.getString("lock_pin", "") ?: "") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("App Pin Protection Lock", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        if (!isDesktop) {
                            IconButton(onClick = { activeSubMode = "MENU" }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.cardBg)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.screenBg)
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("PIN Code Protection Toggle", fontWeight = FontWeight.Bold)
                        Text("Enable security verification on launching Mavo", fontSize = 11.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = pinEnabled,
                        onCheckedChange = {
                            pinEnabled = it
                            sp.edit().putBoolean("pin_enabled", it).apply()
                        }
                    )
                }

                if (pinEnabled) {
                    OutlinedTextField(
                        value = enteredPin,
                        onValueChange = {
                            if (it.length <= 4) {
                                enteredPin = it
                                sp.edit().putString("lock_pin", it).apply()
                            }
                        },
                        label = { Text("Initialize 4-digit security PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth().testTag("app_pin_setup"),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Text("Current stored code will be prompted next time you trigger the app.", fontSize = 11.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        activeSubMode = "MENU"
                        Toast.makeText(context, "Security configurations recorded", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                    Text("Apply Parameters")
                }
            }
        }
    } else if (activeSubMode == "ABOUT") {
        // About & compliance view
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("About Mavo Detail", fontWeight = FontWeight.Bold, color = AppColors.textPrimary) },
                    navigationIcon = {
                        if (!isDesktop) {
                            IconButton(onClick = { activeSubMode = "MENU" }) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppColors.textPrimary)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.cardBg)
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.screenBg)
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_icon),
                    contentDescription = "Mavo",
                    modifier = Modifier.size(72.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = "Mavo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textPrimary
                )
                Text(
                    text = "Record. Transact. Grow.",
                    fontSize = 13.sp,
                    color = AppColors.textSecondary
                )
                Text(
                    text = "Version ${BuildConfig.VERSION_NAME}",
                    fontSize = 12.sp,
                    color = AppColors.textSecondary
                )
                Text(
                    text = "Built for Indian small retailers",
                    fontSize = 11.sp,
                    color = AppColors.textSecondary
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showChangelogDialog = true },
                    colors = CardDefaults.cardColors(containerColor = AppColors.cardBg),
                    border = BorderStroke(1.dp, AppColors.border),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Change Log", fontWeight = FontWeight.SemiBold, color = AppColors.textPrimary)
                            Text(
                                "View the full release history",
                                fontSize = 11.sp,
                                color = AppColors.textSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = AppColors.textTertiary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    } else if (activeSubMode == "THEME") {
            Scaffold(
                containerColor = AppColors.screenBg,
                topBar = {
                    TopAppBar(
                        title = { Text("Theme & Colors", fontWeight = FontWeight.Bold, color = AppColors.textPrimary) },
                        navigationIcon = {
                            if (!isDesktop) {
                                IconButton(onClick = { activeSubMode = "MENU" }) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppColors.textPrimary)
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.cardBg)
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppColors.screenBg)
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AppColors.cardBg),
                        border = BorderStroke(1.dp, AppColors.border),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "App Appearance",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = AppColors.textPrimary
                            )
                            Text(
                                text = "Pick a theme and the whole app updates immediately.",
                                fontSize = 12.sp,
                                color = AppColors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            ThemePickerRow(
                                selectedThemeName = currentTheme.name,
                                onThemeSelected = themeViewModel::setTheme
                            )
                        }
                    }
                }
            }
    } else if (activeSubMode == "FY") {
        val context = LocalContext.current
        val profile = origProfile ?: BusinessProfile(
            businessName = "", ownerName = "", address = "", city = "", state = "West Bengal", stateCode = "19",
            pin = "", phone = "", email = "", gstin = "", pan = "", bankName = "", accountNo = "", ifsc = "",
            logoPath = null, signaturePath = null
        )
        val currentFyLabel = viewModel.financialYear.collectAsState().value
        var fyStartYearText by remember(currentFyLabel) {
            mutableStateOf(currentFyLabel.substringBefore("-"))
        }
        val parsedStartYear = fyStartYearText.toIntOrNull()
        val calculatedFyLabel = remember(parsedStartYear) {
            parsedStartYear?.let { String.format("%04d-%02d", it, (it + 1) % 100) }.orEmpty()
        }
        var showSwitchEditor by remember { mutableStateOf(false) }
        var showCloseConfirm by remember { mutableStateOf(false) }
        val fyRangeText = remember(currentFyLabel) {
            runCatching {
                val fmt = DateTimeFormatter.ofPattern("dd MMM yyyy")
                val s = FinancialYearUtils.startDateFor(currentFyLabel)
                val e = FinancialYearUtils.endDateFor(currentFyLabel)
                "${s.format(fmt)} – ${e.format(fmt)}"
            }.getOrDefault("")
        }
        val saveChanges: () -> Unit = {
            if (parsedStartYear == null || fyStartYearText.length != 4) {
                Toast.makeText(context, "Enter a valid 4-digit financial year start", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.switchFinancialYear(calculatedFyLabel) {
                    viewModel.updateProfile(profile.copy(fyLabel = calculatedFyLabel)) {}
                    Toast.makeText(context, "Active financial year switched to $calculatedFyLabel", Toast.LENGTH_SHORT).show()
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.screenBg)
        ) {
            WizardHeader(
                stepLabel = "",
                onBack = { activeSubMode = "MENU" },
                onClose = { activeSubMode = "MENU" }
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                WizardTitle(
                    text = "Financial Year",
                    subtitle = "Set period, close books, carry forward",
                    modifier = Modifier.padding(top = 12.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = ScreenBorder)
                Spacer(modifier = Modifier.height(20.dp))

                ZbCard {
                    SectionLabel(text = "Active FY")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "FY ${currentFyLabel.replace('-', '–')}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.textPrimary
                            )
                            if (fyRangeText.isNotBlank()) {
                                Text(
                                    text = fyRangeText,
                                    fontSize = 14.sp,
                                    color = AppColors.textSecondary
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = AppColors.textSecondary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ZbCard(contentPadding = 0) {
                    SettingsRow(
                        title = "Switch financial year",
                        subtitle = "Set the start year for your books",
                        onClick = { showSwitchEditor = !showSwitchEditor }
                    )
                    if (showSwitchEditor) {
                        Column(
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 14.dp)
                        ) {
                            ZbField(
                                value = fyStartYearText,
                                onValueChange = { input ->
                                    fyStartYearText = input.filter(Char::isDigit).take(4)
                                },
                                label = "Start year",
                                placeholder = "e.g. 2025",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }
                    HorizontalDivider(color = ScreenBorder)
                    SettingsRow(
                        title = "Close current books",
                        subtitle = "Freeze postings & carry forward balances",
                        onClick = { showCloseConfirm = true }
                    )
                    // ponytail: the mockup's "Books beginning date" and "Cutoff for edits"
                    // rows are omitted — neither value exists in the schema. Upgrade path:
                    // persist them in AppPreferences when a books-lock feature is built.
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.cardBg)
            ) {
                HorizontalDivider(color = ScreenBorder)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SecondaryButton(
                        label = "Cancel",
                        onClick = { activeSubMode = "MENU" },
                        modifier = Modifier.weight(1f)
                    )
                    PrimaryButton(
                        label = "Save changes",
                        onClick = saveChanges,
                        modifier = Modifier.weight(2f)
                    )
                }
            }
        }

        if (showCloseConfirm) {
            AlertDialog(
                onDismissRequest = { showCloseConfirm = false },
                title = { Text("Close current books?") },
                text = {
                    Text(
                        "Freeze FY ${currentFyLabel.replace('-', '–')} and carry balances forward to " +
                            "FY ${FinancialYearUtils.nextFinancialYear(currentFyLabel).replace('-', '–')}? " +
                            "Postings for this year will be locked."
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        showCloseConfirm = false
                        viewModel.closeFinancialYear(
                            sourceFinancialYearCode = currentFyLabel,
                            lockSourceYear = true,
                            onSuccess = { result ->
                                val target = result.targetFinancialYearCode
                                viewModel.switchFinancialYear(target) {
                                    viewModel.updateProfile(profile.copy(fyLabel = target)) {}
                                }
                                Toast.makeText(
                                    context,
                                    "FY ${result.sourceFinancialYearCode} closed — " +
                                        "${result.ledgerBalancesCarried} ledger balances carried forward",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            onError = { e ->
                                Toast.makeText(context, "Close failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }) {
                        Text("Close books")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCloseConfirm = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    } else if (activeSubMode == "EMAIL") {
        EmailAutomationSection(viewModel = viewModel)
    }
}

    if (isDesktop) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.width(360.dp).fillMaxHeight()) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Application Settings", fontWeight = FontWeight.Bold, color = AppColors.textPrimary) },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.cardBg)
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        SettingsMenuSection(
                            viewModel = viewModel,
                            currentThemeLabel = currentTheme.name.lowercase()
                                .replaceFirstChar { it.uppercase(Locale.getDefault()) },
                            onSelect = { activeSubMode = it },
                            openChangeLog = { showChangelogDialog = true },
                            navigateToProducts = navigateToProducts,
                            navigateToLedgerBooks = navigateToLedgerBooks,
                            navigateToParties = navigateToParties
                        )
                    }
                }
            }
            Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(AppColors.border))
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                DetailContent()
            }
        }
    } else {
        if (activeSubMode == "MENU") {
            // Root settings tab: big title + profile hero live in the content (mockup 15),
            // so there is no top app bar and no back button on this tab.
            SettingsMenuSection(
                viewModel = viewModel,
                currentThemeLabel = currentTheme.name.lowercase()
                    .replaceFirstChar { it.uppercase(Locale.getDefault()) },
                onSelect = { activeSubMode = it },
                openChangeLog = { showChangelogDialog = true },
                navigateToProducts = navigateToProducts,
                navigateToLedgerBooks = navigateToLedgerBooks,
                navigateToParties = navigateToParties
            )
        } else {
            DetailContent()
        }
    }

    if (showChangelogDialog && changelogData != null) {
        AlertDialog(
            onDismissRequest = { showChangelogDialog = false },
            confirmButton = {
                Button(onClick = { showChangelogDialog = false }) {
                    Text("Close")
                }
            },
            title = {
                Text("Change Log")
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    changelogData?.history?.forEach { entry ->
                        Text(
                            text = "Version ${entry.version}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        entry.changes.forEach { change ->
                            Text(
                                text = "• $change",
                                color = Color(0xFF111827)
                            )
                        }
                    }
                }
            },
            containerColor = Color.White,
            textContentColor = Color(0xFF111827),
            titleContentColor = Color(0xFF111827)
        )
    }
}

@Composable
fun SettingsMenuSection(
    viewModel: AppViewModel,
    currentThemeLabel: String,
    onSelect: (String) -> Unit,
    openChangeLog: () -> Unit,
    navigateToProducts: () -> Unit,
    navigateToLedgerBooks: () -> Unit,
    navigateToParties: () -> Unit
) {
    val scrollState = rememberScrollState()
    val profile by viewModel.profile.collectAsState()
    val currentFy = viewModel.financialYear.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.screenBg)
            .verticalScroll(scrollState)
            .imePadding()
            .statusBarsPadding()
            .padding(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Big screen title + profile hero (mockup 15): no colored hero card,
        // just an initials circle, business name and proprietor line.
        WizardTitle(
            text = "Settings",
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(AppColors.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile?.businessName?.filter { it.isLetter() }?.take(2)?.uppercase(Locale.getDefault()) ?: "--",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textOnPrimary
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile?.businessName?.ifBlank { "Business Profile" } ?: "Business Profile",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val proprietor = profile?.ownerName.orEmpty()
                if (proprietor.isNotBlank()) {
                    Text(
                        text = proprietor,
                        fontSize = 15.sp,
                        color = AppColors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = ScreenBorder)
        Spacer(modifier = Modifier.height(24.dp))

        // Menu items section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.screenBg)
                .padding(start = 20.dp, top = 4.dp, end = 20.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            SettingsGroup(label = "Profile & master data") {
                SettingsRow(
                    icon = Icons.Outlined.Person,
                    title = "Business Profile",
                    subtitle = profile?.businessName?.ifBlank { null },
                    onClick = { onSelect("BUSINESS") }
                )
                HorizontalDivider(color = ScreenBorder)
                SettingsRow(
                    icon = Icons.Outlined.Inventory2,
                    title = "Products",
                    onClick = navigateToProducts
                )
                HorizontalDivider(color = ScreenBorder)
                SettingsRow(
                    icon = Icons.AutoMirrored.Outlined.MenuBook,
                    title = "Chart of Accounts",
                    onClick = navigateToLedgerBooks
                )
                HorizontalDivider(color = ScreenBorder)
                SettingsRow(
                    icon = Icons.Outlined.People,
                    title = "Party Master",
                    onClick = navigateToParties
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsGroup(label = "Appearance") {
                SettingsRow(
                    icon = Icons.Outlined.Palette,
                    title = "Theme & Colors",
                    subtitle = currentThemeLabel,
                    onClick = { onSelect("THEME") }
                )
                HorizontalDivider(color = ScreenBorder)
                SettingsRow(
                    icon = Icons.Outlined.Tune,
                    title = "Dashboard Options",
                    onClick = { onSelect("CUSTOMIZE") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsGroup(label = "Financial") {
                SettingsRow(
                    icon = Icons.Outlined.CalendarMonth,
                    title = "Financial Year",
                    subtitle = "FY ${currentFy.replace('-', '–')}",
                    onClick = { onSelect("FY") }
                )
                HorizontalDivider(color = ScreenBorder)
                // ponytail: no dedicated GST screen exists — GSTIN/PAN/state code live
                // in Business Profile. Upgrade path: a standalone GST & tax setup screen.
                SettingsRow(
                    icon = Icons.Outlined.Receipt,
                    title = "GST & Tax Setup",
                    onClick = { onSelect("BUSINESS") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsGroup(label = "Data") {
                SettingsRow(
                    icon = Icons.Outlined.Storage,
                    title = "Data Management",
                    onClick = { onSelect("DATA") }
                )
                HorizontalDivider(color = ScreenBorder)
                SettingsRow(
                    icon = Icons.Outlined.History,
                    title = "Change Log",
                    onClick = openChangeLog
                )
                HorizontalDivider(color = ScreenBorder)
                SettingsRow(
                    icon = Icons.Default.Info,
                    title = "About Mavo",
                    onClick = { onSelect("ABOUT") }
                )
            }
        }
    }
}

@Composable
private fun SettingsGroup(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionLabel(text = label)
        Spacer(modifier = Modifier.height(10.dp))
        ZbCard(contentPadding = 0, content = content)
    }
}

@Composable
private fun SettingsRow(
    title: String,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    subtitle: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppColors.textSecondary,
                modifier = Modifier.size(22.dp)
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.textPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = AppColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = AppColors.textTertiary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun DataManagementContent(
    viewModel: AppViewModel,
    exportCsv: () -> Unit,
    importCsv: () -> Unit,
    recordCount: Int,
    onBack: () -> Unit,
    onAuditLog: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sp = context.getSharedPreferences("mavo_pref", Context.MODE_PRIVATE)
    var lastBackupAt by remember { mutableStateOf(sp.getLong("last_backup_at", 0L)) }
    val restoreUri = remember { mutableStateOf<Uri?>(null) }
    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri -> if (uri != null) restoreUri.value = uri }
    val dbFile = remember {
        context.getDatabasePath("Mavo.db")
            .let { if (it.exists()) it else context.getDatabasePath("ZeroBook.db") }
    }
    val dbSizeMb = dbFile.length() / (1024.0 * 1024.0)
    val backupText = if (lastBackupAt > 0L) {
        " · Last backup " + DateUtils.getRelativeTimeSpanString(
            lastBackupAt, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS
        )
    } else {
        ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.screenBg)
    ) {
        WizardHeader(stepLabel = "", onBack = onBack, onClose = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            WizardTitle(
                text = "Data Management",
                subtitle = "Backup, restore, export & wipe",
                modifier = Modifier.padding(top = 12.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = ScreenBorder)
            Spacer(modifier = Modifier.height(20.dp))

            ZbCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(AppColors.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Storage,
                            contentDescription = null,
                            tint = AppColors.textOnPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Local database",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.textPrimary
                        )
                        Text(
                            text = "$recordCount records · %.1f MB%s".format(Locale.US, dbSizeMb, backupText),
                            fontSize = 14.sp,
                            color = AppColors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ZbCard(contentPadding = 0) {
                SettingsRow(
                    icon = Icons.Outlined.FileDownload,
                    title = "Backup now",
                    subtitle = "Full database copy to Downloads/Mavo/Backups",
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            val result = viewModel.backupDatabase(context)
                            withContext(Dispatchers.Main) {
                                if (result != null) {
                                    sp.edit().putLong("last_backup_at", System.currentTimeMillis()).apply()
                                    lastBackupAt = System.currentTimeMillis()
                                    Toast.makeText(context, "Backup saved to ${result.locationLabel}", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Backup failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                )
                HorizontalDivider(color = ScreenBorder)
                SettingsRow(
                    icon = Icons.Outlined.FileUpload,
                    title = "Restore from backup",
                    subtitle = "Overwrites current data",
                    onClick = { restoreLauncher.launch(arrayOf("*/*")) }
                )
                HorizontalDivider(color = ScreenBorder)
                SettingsRow(
                    icon = Icons.Outlined.Description,
                    title = "Export as CSV / Excel",
                    subtitle = "Vouchers, parties, stock",
                    onClick = exportCsv
                )
                HorizontalDivider(color = ScreenBorder)
                SettingsRow(
                    icon = Icons.Outlined.Description,
                    title = "Import from CSV",
                    subtitle = "Add vouchers, parties, stock from a file",
                    onClick = importCsv
                )
                HorizontalDivider(color = ScreenBorder)
                SettingsRow(
                    icon = Icons.Outlined.History,
                    title = "Audit log",
                    subtitle = "Financial year close events",
                    onClick = onAuditLog
                )
            }
            // ponytail: the mockup's "Danger zone / Delete all data" card is omitted —
            // wiping the database needs explicit developer approval (AGENTS.md).
            // Upgrade path: build it behind a typed confirmation once approved.

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    val pendingUri = restoreUri.value
    if (pendingUri != null) {
        AlertDialog(
            onDismissRequest = { restoreUri.value = null },
            title = { Text("Restore backup?") },
            text = {
                Text(
                    "This overwrites all current data with the selected backup. " +
                        "Restart Mavo afterwards to load the restored database."
                )
            },
            confirmButton = {
                Button(onClick = {
                    restoreUri.value = null
                    scope.launch(Dispatchers.IO) {
                        val path = copyUriToInternalStorage(
                            context, pendingUri, "restore_${System.currentTimeMillis()}.db"
                        )
                        val ok = path != null && viewModel.restoreDatabase(context, File(path))
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                if (ok) "Backup restored — restart Mavo to load it" else "Restore failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }) {
                    Text("Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { restoreUri.value = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AuditLogContent(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val fy = viewModel.financialYear.collectAsState().value
    val logs by remember(fy) { viewModel.auditLogsForYear(fy) }
        .collectAsState(initial = emptyList())
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.screenBg)
    ) {
        WizardHeader(stepLabel = "", onBack = onBack, onClose = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            WizardTitle(
                text = "Audit log",
                subtitle = "Financial year close events",
                modifier = Modifier.padding(top = 12.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = ScreenBorder)
            Spacer(modifier = Modifier.height(20.dp))

            if (logs.isEmpty()) {
                EmptyState(
                    icon = Icons.Outlined.History,
                    title = "No audit events",
                    message = "Year close events for FY ${fy.replace('-', '–')} will appear here."
                )
            } else {
                ZbCard(contentPadding = 0) {
                    logs.forEachIndexed { index, log ->
                        if (index > 0) HorizontalDivider(color = ScreenBorder)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = if (log.action == "YEAR_CLOSE_COMPLETED") "Year closed" else log.action,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = AppColors.textPrimary
                                )
                                Text(
                                    text = "${log.financialYearCode.replace('-', '–')} → " +
                                        (log.targetFinancialYearCode?.replace('-', '–') ?: "—"),
                                    fontSize = 14.sp,
                                    color = AppColors.textSecondary
                                )
                            }
                            Text(
                                text = dateFormat.format(Date(log.createdAt)),
                                fontSize = 13.sp,
                                color = AppColors.textTertiary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressTrackerSettingsScreen(onBackToMenu: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var enabled by remember { mutableStateOf(false) }
    var metric by remember { mutableStateOf("Sales") }
    var period by remember { mutableStateOf("Monthly") }
    var target by remember { mutableStateOf("200000") }
    var kpiAnimationMode by remember { mutableStateOf("WALLET_STACK") }

    LaunchedEffect(Unit) {
        enabled = AppPreferences.isProgressTrackerEnabled(context)
        metric = AppPreferences.getProgressTrackerMetric(context)
        period = AppPreferences.getProgressTrackerPeriod(context)
        target = AppPreferences.getProgressTrackerTarget(context)
        kpiAnimationMode = AppPreferences.getKpiAnimationMode(context)
    }

    Scaffold(
        containerColor = AppColors.screenBg,
        topBar = {
            TopAppBar(
                title = { Text("Customize", fontWeight = FontWeight.Bold, color = AppColors.textPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBackToMenu) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AppColors.textPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.cardBg)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.screenBg)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, AppColors.border, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = AppColors.cardBg)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Dashboard", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppColors.textPrimary)
                    Text("Configure dashboard appearance and KPI card animation style.", fontSize = 12.sp, color = AppColors.textSecondary)

                    Text("KPI Animation Mode", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = AppColors.textSecondary)
                    val animationModes = listOf(
                        "STANDARD_HORIZONTAL" to "Standard Horizontal",
                        "WALLET_STACK" to "Wallet Stack (Default)",
                        "SPOTLIGHT_CAROUSEL" to "Spotlight Carousel"
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        animationModes.forEach { (mode, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        1.dp,
                                        if (kpiAnimationMode == mode) AppColors.primary else AppColors.border,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        kpiAnimationMode = mode
                                        scope.launch { AppPreferences.setKpiAnimationMode(context, mode) }
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(label, fontSize = 13.sp, color = AppColors.textPrimary, fontWeight = if (kpiAnimationMode == mode) FontWeight.Bold else FontWeight.Normal)
                                if (kpiAnimationMode == mode) {
                                    Icon(Icons.Default.Check, contentDescription = "Selected", tint = AppColors.primary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    HorizontalDivider()

                    Text("Progress Tracker", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AppColors.textPrimary)
                    Text("Show a progress tracker on the dashboard connected to live accounting data.", fontSize = 12.sp, color = AppColors.textSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Show Progress Tracker", fontWeight = FontWeight.Medium, color = AppColors.textPrimary)
                        Switch(
                            checked = enabled,
                            onCheckedChange = {
                                enabled = it
                                scope.launch { AppPreferences.setProgressTrackerEnabled(context, it) }
                            }
                        )
                    }
                    OutlinedTextField(
                        value = metric,
                        onValueChange = { metric = it },
                        label = { Text("Metric") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = period,
                        onValueChange = { period = it },
                        label = { Text("Time") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = target,
                        onValueChange = { target = it.filter(Char::isDigit) },
                        label = { Text("Target Amount") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Button(
                        onClick = {
                            scope.launch {
                                AppPreferences.setProgressTrackerEnabled(context, enabled)
                                AppPreferences.setProgressTrackerMetric(context, metric.ifBlank { "Sales" })
                                AppPreferences.setProgressTrackerPeriod(context, period.ifBlank { "Monthly" })
                                AppPreferences.setProgressTrackerTarget(context, target.ifBlank { "200000" })
                                AppPreferences.setKpiAnimationMode(context, kpiAnimationMode)
                                Toast.makeText(context, "Dashboard settings saved", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary, contentColor = AppColors.textOnPrimary)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemePickerRow(
    selectedThemeName: String,
    onThemeSelected: (String) -> Unit
) {
    val themeOptions = listOf(
        Triple("ZERO", "Zero", Color(0xFF0A0A0A)),
        Triple("SAFFRON", "Saffron", Color(0xFFFDF6EC)),
        Triple("SLATE", "Slate", Color(0xFF22755F)),
        Triple("LEDGER", "Ledger", Color(0xFF22A06B)),
        Triple("INK", "Ink", Color(0xFF7C3AED)),
        Triple("NIGHT", "Night", Color(0xFF18211F))
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        themeOptions.forEach { (name, label, swatch) ->
            val isSelected = selectedThemeName == name
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .weight(1f)
                    .premiumClickable { onThemeSelected(name) }
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(21.dp))
                        .background(swatch)
                        .border(
                            width = if (name == "SAFFRON") 2.dp else 1.dp,
                            color = when {
                                isSelected -> AppColors.primary
                                name == "SAFFRON" -> Color(0xFF8D6E63)
                                else -> AppColors.border
                            },
                            shape = RoundedCornerShape(21.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = label,
                            tint = if (name == "SAFFRON") AppColors.textPrimary else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = AppColors.textSecondary
                )
            }
        }
    }
}
