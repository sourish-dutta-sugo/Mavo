package com.mavo.app.ui.screens.tablet

import com.mavo.app.ui.screens.BusinessProfileSettingsSection
import com.mavo.app.ui.screens.EmailAutomationSection

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
fun TabletSettingsScreen(
    viewModel: AppViewModel,
    themeViewModel: ThemeViewModel,
    isDesktop: Boolean = false,
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
        if (activeSubMode == "LOCK") {
        // App security control
        val sp = context.getSharedPreferences("mavo_pref", Context.MODE_PRIVATE)
        var pinEnabled by remember { mutableStateOf(sp.getBoolean("pin_enabled", false)) }
        var enteredPin by remember { mutableStateOf(sp.getString("lock_pin", "") ?: "") }

        Scaffold(
            containerColor = Color.Transparent,
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
                    .background(Color.Transparent)
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

                Spacer(modifier = Modifier.height(12.dp))
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
            containerColor = Color.Transparent,
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
                    painter = painterResource(R.drawable.ic_mavo_logo),
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
                            .padding(horizontal = 16.dp, vertical = 16.dp),
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
                            modifier = Modifier.size(24.dp)
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
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
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
                    containerColor = Color.Transparent,
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
                            openChangeLog = { showChangelogDialog = true }
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
                openChangeLog = { showChangelogDialog = true }
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
                            color = AppColors.textPrimary
                        )
                        entry.changes.forEach { change ->
                            Text(
                                text = "• $change",
                                color = AppColors.textSecondary
                            )
                        }
                    }
                }
            },
            containerColor = AppColors.cardBg,
            textContentColor = AppColors.textPrimary,
            titleContentColor = AppColors.textPrimary
        )
    }
}

@Composable
fun SettingsMenuSection(
    viewModel: AppViewModel,
    currentThemeLabel: String,
    onSelect: (String) -> Unit,
    openChangeLog: () -> Unit
) {
    val scrollState = rememberScrollState()
    val profile by viewModel.profile.collectAsState()
    val currentFy = viewModel.financialYear.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .verticalScroll(scrollState)
            .imePadding()
            .statusBarsPadding()
            .padding(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        // Keep the business identity on an opaque surface above the shared canvas.
        WizardTitle(
            text = "Settings",
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.cardBg),
            border = BorderStroke(1.dp, AppColors.border),
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
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = ScreenBorder)
        Spacer(modifier = Modifier.height(24.dp))

        // Menu items section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
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
        Spacer(modifier = Modifier.height(12.dp))
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
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppColors.textSecondary,
                modifier = Modifier.size(24.dp)
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
            .background(Color.Transparent)
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
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
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
            .background(Color.Transparent)
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
                                .padding(horizontal = 16.dp, vertical = 16.dp),
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
    var enabled by remember { mutableStateOf(true) }
    var metric by remember { mutableStateOf("Sales") }
    var period by remember { mutableStateOf("Monthly") }
    var target by remember { mutableStateOf("200000") }

    LaunchedEffect(Unit) {
        enabled = AppPreferences.isProgressTrackerEnabled(context)
        metric = AppPreferences.getProgressTrackerMetric(context)
        period = AppPreferences.getProgressTrackerPeriod(context)
        target = AppPreferences.getProgressTrackerTarget(context)
    }

    Scaffold(
        containerColor = Color.Transparent,
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
                .background(Color.Transparent)
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
                    Text("Configure dashboard appearance and progress tracker settings.", fontSize = 12.sp, color = AppColors.textSecondary)

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
                                Toast.makeText(context, "Dashboard settings saved", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
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
        AppTheme.ZERO to "Zero",
        AppTheme.SAFFRON to "Saffron",
        AppTheme.SLATE to "Slate",
        AppTheme.LEDGER to "Ledger"
    ).map { (theme, label) -> Triple(theme.name, label, theme.accentPrimary) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        themeOptions.forEach { (name, label, swatch) ->
            val isSelected = selectedThemeName == name
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .premiumClickable { onThemeSelected(name) }
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(swatch)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) AppColors.primary else AppColors.border,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = label,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
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
