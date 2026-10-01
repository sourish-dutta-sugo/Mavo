package com.zerobook.app.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zerobook.app.data.Expense
import com.zerobook.app.data.Income
import com.zerobook.app.data.Utils
import com.zerobook.app.data.filterDecimalInput
import com.zerobook.app.services.ExportStorageManager
import com.zerobook.app.services.ExportTarget
import com.zerobook.app.ui.AppViewModel
import com.zerobook.app.ui.animation.premiumFabEntrance
import com.zerobook.app.ui.animation.pressScale
import com.zerobook.app.ui.theme.AppColors
import java.io.File
import java.util.Calendar
import java.util.UUID

private val incomeCategories = listOf(
    "Sales Commission", "Interest Received", "Rental Income", "Freelance Services",
    "Consulting Fees", "Investment Returns", "Grants", "Donations",
    "Refunds Received", "Other Income"
)

private val expenseCategories = listOf(
    "Rent", "Electricity", "Water", "Telephone", "Internet",
    "Staff Salary", "Transport", "Packaging", "Maintenance",
    "Office Supplies", "Advertising", "Insurance", "Bank Charges",
    "Miscellaneous", "Other"
)

private data class LedgerRow(
    val id: String,
    val date: Long,
    val category: String,
    val description: String,
    val amount: Double,
    val paymentMode: String,
    val referenceNo: String
)

private data class LedgerDraft(
    val id: String,
    val date: Long,
    val category: String,
    val description: String,
    val amount: Double,
    val paymentMode: String,
    val referenceNo: String,
    val attachmentPath: String,
    val voucherNo: String
)

private class LedgerKind(
    val title: String,
    val entryTitle: String,
    val noLabel: String,
    val missingNoMessage: String,
    val saveButtonLabel: String,
    val fabDescription: String,
    val csvFileName: String,
    val voucherType: String,
    val categories: List<String>,
    val save: (AppViewModel, LedgerDraft, onSuccess: () -> Unit) -> Unit
)

private val IncomeKind = LedgerKind(
    title = "Income",
    entryTitle = "Add Income",
    noLabel = "Income No",
    missingNoMessage = "Cannot save: Income No is missing!",
    saveButtonLabel = "Save Income",
    fabDescription = "Add income",
    csvFileName = "ZeroBook_Income.csv",
    voucherType = "INCOME",
    categories = incomeCategories,
    save = { viewModel, draft, onSuccess ->
        viewModel.saveIncome(
            Income(
                id = draft.id,
                date = draft.date,
                category = draft.category,
                description = draft.description,
                amount = draft.amount,
                paymentMode = draft.paymentMode,
                referenceNo = draft.referenceNo,
                attachmentPath = draft.attachmentPath,
                voucherNo = draft.voucherNo
            ),
            onSuccess
        )
    }
)

private val ExpenseKind = LedgerKind(
    title = "Expenses",
    entryTitle = "Add Expense",
    noLabel = "Expense No",
    missingNoMessage = "Cannot save: Expense No is missing!",
    saveButtonLabel = "Save Expense",
    fabDescription = "Add expense",
    csvFileName = "ZeroBook_Expenses.csv",
    voucherType = "EXPENSE",
    categories = expenseCategories,
    save = { viewModel, draft, onSuccess ->
        viewModel.saveExpense(
            Expense(
                id = draft.id,
                date = draft.date,
                category = draft.category,
                description = draft.description,
                amount = draft.amount,
                paymentMode = draft.paymentMode,
                referenceNo = draft.referenceNo,
                attachmentPath = draft.attachmentPath,
                voucherNo = draft.voucherNo
            ),
            onSuccess
        )
    }
)

private fun Income.toLedgerRow() = LedgerRow(id, date, category, description, amount, paymentMode, referenceNo)
private fun Expense.toLedgerRow() = LedgerRow(id, date, category, description, amount, paymentMode, referenceNo)

@Composable
fun IncomeScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit
) {
    val incomes by viewModel.incomes.collectAsState()
    LedgerListScreen(IncomeKind, incomes.map { it.toLedgerRow() }, viewModel, onNavigateBack)
}

@Composable
fun ExpensesScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit
) {
    val expenses by viewModel.expenses.collectAsState()
    LedgerListScreen(ExpenseKind, expenses.map { it.toLedgerRow() }, viewModel, onNavigateBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LedgerListScreen(
    kind: LedgerKind,
    rows: List<LedgerRow>,
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var activeFilter by remember { mutableStateOf("ALL") }
    val filteredRows = remember(rows, activeFilter) {
        rows.filter { activeFilter == "ALL" || it.category == activeFilter }
    }

    if (showForm) {
        LedgerEntryScreen(kind = kind, viewModel = viewModel, onDismiss = { showForm = false })
        return
    }

    Scaffold(
        containerColor = AppColors.screenBg,
        topBar = {
            TopAppBar(
                title = { Text(kind.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.cardBg)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showForm = true },
                containerColor = AppColors.primary,
                modifier = Modifier
                    .premiumFabEntrance()
                    .pressScale()
            ) {
                Icon(Icons.Default.Add, contentDescription = kind.fabDescription, tint = AppColors.textOnPrimary)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.screenBg)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                FilterChip(selected = activeFilter == "ALL", onClick = { activeFilter = "ALL" }, label = { Text("All") })
                kind.categories.take(4).forEach { category ->
                    FilterChip(selected = activeFilter == category, onClick = { activeFilter = category }, label = { Text(category) })
                }
            }
            Button(
                onClick = {
                    val csv = buildString {
                        append("date,category,description,amount,payment_mode,reference\n")
                        filteredRows.forEach { row ->
                            append("${Utils.formatDate(row.date)},${row.category},${row.description},${row.amount},${row.paymentMode},${row.referenceNo}\n")
                        }
                    }
                    val result = ExportStorageManager.exportBytes(
                        context = viewModel.getApplication(),
                        bytes = csv.toByteArray(),
                        displayName = kind.csvFileName,
                        mimeType = "text/csv",
                        target = ExportTarget.Reports
                    )
                    Toast.makeText(viewModel.getApplication(), "Saved to ${result.locationLabel}", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier.pressScale()
            ) {
                Text("Export to CSV")
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(filteredRows, key = { it.id }) { row ->
                    Card(colors = CardDefaults.cardColors(containerColor = AppColors.cardBg)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(row.category, fontWeight = FontWeight.Bold)
                                Text(row.description.ifBlank { "No description" }, color = AppColors.textSecondary)
                                Text("${Utils.formatDate(row.date)} | ${row.paymentMode}", color = AppColors.textSecondary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(Utils.formatIndianCurrency(row.amount), fontWeight = FontWeight.Bold, color = AppColors.primary)
                                if (row.referenceNo.isNotBlank()) Text(row.referenceNo, color = AppColors.textSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LedgerEntryScreen(
    kind: LedgerKind,
    viewModel: AppViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var date by remember { mutableStateOf(System.currentTimeMillis()) }
    var dateText by remember { mutableStateOf(Utils.formatDate(System.currentTimeMillis())) }
    var isDateEditing by remember { mutableStateOf(false) }
    var voucherNo by remember { mutableStateOf("") }
    var voucherNoTouched by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf(kind.categories.first()) }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf("CASH") }
    var referenceNo by remember { mutableStateOf("") }
    var attachmentPath by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    val attachLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        attachmentPath = uri?.toString().orEmpty()
    }

    LaunchedEffect(date, voucherNoTouched) {
        if (!voucherNoTouched) {
            voucherNo = viewModel.generateNextVoucherNo(kind.voucherType, date)
        }
    }

    Scaffold(
        containerColor = AppColors.screenBg,
        topBar = {
            TopAppBar(
                title = { Text(kind.entryTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isDateEditing) {
                val dateFocusReq = remember { FocusRequester() }
                val focusMgr = LocalFocusManager.current
                LaunchedEffect(Unit) { dateFocusReq.requestFocus() }
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { newText ->
                        dateText = newText
                        val parsed = Utils.parseShorthandDate(newText)
                        if (parsed != null) { date = parsed }
                    },
                    label = { Text("Date") },
                    trailingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Pick date",
                            modifier = Modifier.clickable {
                                val cal = Calendar.getInstance().apply { timeInMillis = date }
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        val picked = Calendar.getInstance().apply {
                                            set(year, month, day, 0, 0, 0)
                                            set(Calendar.MILLISECOND, 0)
                                        }
                                        date = picked.timeInMillis
                                        dateText = Utils.formatDate(picked.timeInMillis)
                                        isDateEditing = false
                                        focusMgr.clearFocus()
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val parsed = Utils.parseShorthandDate(dateText)
                            if (parsed != null) {
                                date = parsed
                                dateText = Utils.formatDate(parsed)
                            } else {
                                dateText = Utils.formatDate(date)
                            }
                            isDateEditing = false
                            focusMgr.clearFocus()
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(dateFocusReq)
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused && !isDateEditing) {
                                val parsed = Utils.parseShorthandDate(dateText)
                                if (parsed != null) {
                                    date = parsed
                                    dateText = Utils.formatDate(parsed)
                                } else {
                                    dateText = Utils.formatDate(date)
                                }
                            }
                        }
                )
            } else {
                OutlinedTextField(
                    value = dateText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date") },
                    trailingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Pick date",
                            modifier = Modifier.clickable {
                                val cal = Calendar.getInstance().apply { timeInMillis = date }
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        val picked = Calendar.getInstance().apply {
                                            set(year, month, day, 0, 0, 0)
                                            set(Calendar.MILLISECOND, 0)
                                        }
                                        date = picked.timeInMillis
                                        dateText = Utils.formatDate(picked.timeInMillis)
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isDateEditing = true }
                )
            }
            OutlinedTextField(
                value = voucherNo,
                onValueChange = {
                    voucherNoTouched = true
                    voucherNo = it
                },
                label = { Text(kind.noLabel) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.padding(8.dp)) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 0.dp)
            )
            DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                kind.categories.forEach { option ->
                    DropdownMenuItem(text = { Text(option) }, onClick = { category = option; categoryExpanded = false })
                }
            }
            Button(onClick = { categoryExpanded = true }) { Text("Choose Category") }
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = filterDecimalInput(it) },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                listOf("CASH", "BANK", "UPI", "CHEQUE").forEach { mode ->
                    FilterChip(selected = paymentMode == mode, onClick = { paymentMode = mode }, label = { Text(mode) })
                }
            }
            OutlinedTextField(value = referenceNo, onValueChange = { referenceNo = it }, label = { Text("Reference No") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { attachLauncher.launch(arrayOf("image/*", "application/pdf")) }, colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)) {
                Icon(Icons.Default.AttachFile, contentDescription = null, tint = AppColors.textOnPrimary)
                Text(if (attachmentPath.isBlank()) "Upload Receipt" else File(attachmentPath).name, color = AppColors.textOnPrimary)
            }
            Button(
                onClick = {
                    val amountValue = amount.toDoubleOrNull() ?: 0.0
                    if (amountValue <= 0.0) {
                        Toast.makeText(viewModel.getApplication(), "Enter a valid amount", Toast.LENGTH_SHORT).show()
                    } else if (voucherNo.trim().isBlank()) {
                        Toast.makeText(viewModel.getApplication(), kind.missingNoMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        kind.save(
                            viewModel,
                            LedgerDraft(
                                id = UUID.randomUUID().toString(),
                                date = date,
                                category = category,
                                description = description,
                                amount = amountValue,
                                paymentMode = paymentMode,
                                referenceNo = referenceNo,
                                attachmentPath = attachmentPath,
                                voucherNo = voucherNo.trim()
                            )
                        ) {
                            onDismiss()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)
            ) {
                Text(kind.saveButtonLabel, color = AppColors.textOnPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}
