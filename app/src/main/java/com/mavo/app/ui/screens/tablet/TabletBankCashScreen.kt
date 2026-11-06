package com.mavo.app.ui.screens.tablet

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mavo.app.data.BankCashTransaction
import com.mavo.app.data.Party
import com.mavo.app.data.Utils
import com.mavo.app.data.filterDecimalInput
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.LocalAppWindowSizeClass
import com.mavo.app.ui.UiLoadState
import com.mavo.app.ui.animation.premiumClickable
import com.mavo.app.ui.animation.premiumFabEntrance
import com.mavo.app.ui.animation.pressScale
import com.mavo.app.ui.components.UiLoadStateNotice
import com.mavo.app.ui.components.blocksContent
import com.mavo.app.ui.components.valueOrEmpty
import com.mavo.app.ui.dashboardMaxWidthDp
import com.mavo.app.ui.theme.AppColors
import java.util.UUID

/**
 * Full Tablet screen view for Bank & Cash screen.
 * Supports Portrait & Landscape: in landscape, account balances and registers adapt cleanly
 * within a max-width constrained container with generous 24dp margins.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabletBankCashScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = LocalAppWindowSizeClass.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val transactionsReadState by viewModel.transactionsUiState.collectAsState()
    val ledgerEntriesReadState by viewModel.ledgerEntriesUiState.collectAsState()
    val partiesReadState by viewModel.partiesUiState.collectAsState()
    val transactions = transactionsReadState.valueOrEmpty()
    val ledgerEntries = ledgerEntriesReadState.valueOrEmpty()
    val parties = partiesReadState.valueOrEmpty()

    var activeTab by remember { mutableStateOf("CASH") }
    var showAddForm by remember { mutableStateOf(false) }

    val (cashTotal, bankTotal) = remember(ledgerEntries) {
        var cash = 0.0
        var bank = 0.0
        ledgerEntries.forEach { entry ->
            val delta = entry.debit - entry.credit
            if (entry.accountHead == "Cash") {
                cash += delta
            } else if (entry.accountHead == "Bank") {
                bank += delta
            }
        }
        cash to bank
    }

    val matchedTransactions = remember(transactions, activeTab) {
        transactions.filter { tx ->
            if (activeTab == "CASH") tx.mode == "CASH" else tx.mode != "CASH"
        }
    }

    if (showAddForm) {
        TabletAddTransactionForm(
            viewModel = viewModel,
            parties = parties,
            partiesReadState = partiesReadState,
            onDismiss = { showAddForm = false },
            modifier = modifier
        )
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = AppColors.screenBg,
            topBar = {
                TopAppBar(
                    title = { Text("Bank & Cash Register", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AppColors.cardBg,
                        titleContentColor = AppColors.textPrimary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddForm = true },
                    containerColor = AppColors.primary,
                    contentColor = AppColors.textOnPrimary,
                    modifier = Modifier.testTag("add_manual_tx_fab").premiumFabEntrance().pressScale()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Transaction")
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = windowSizeClass.dashboardMaxWidthDp()?.dp ?: Dp.Unspecified)
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    // Account Balance Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .premiumClickable { activeTab = "CASH" }
                                .border(
                                    width = if (activeTab == "CASH") 2.dp else 1.dp,
                                    color = if (activeTab == "CASH") AppColors.primary else AppColors.border,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            colors = CardDefaults.cardColors(containerColor = AppColors.cardBg),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text("Cash Account", fontSize = 14.sp, color = AppColors.textSecondary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    Utils.formatIndianCurrency(cashTotal),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (cashTotal >= 0) AppColors.success else AppColors.error
                                )
                            }
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .premiumClickable { activeTab = "BANK" }
                                .border(
                                    width = if (activeTab == "BANK") 2.dp else 1.dp,
                                    color = if (activeTab == "BANK") AppColors.primary else AppColors.border,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            colors = CardDefaults.cardColors(containerColor = AppColors.cardBg),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text("Bank & Online", fontSize = 14.sp, color = AppColors.textSecondary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    Utils.formatIndianCurrency(bankTotal),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (bankTotal >= 0) AppColors.success else AppColors.error
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (activeTab == "CASH") "Recent Cash Movements" else "Bank & Online Transactions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = AppColors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    UiLoadStateNotice(transactionsReadState, viewModel::retryFailedReads)
                    if (transactionsReadState.blocksContent() && matchedTransactions.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = AppColors.primary)
                        }
                    } else if (matchedTransactions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No transactions logged in this tab yet", color = AppColors.textHint, fontSize = 15.sp)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(matchedTransactions) { tx ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = AppColors.cardBg),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.border)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(18.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .background(
                                                        if (tx.type == "RECEIPT") AppColors.success.copy(alpha = 0.12f)
                                                        else AppColors.error.copy(alpha = 0.12f),
                                                        shape = RoundedCornerShape(10.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = if (tx.type == "RECEIPT") Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                                    contentDescription = null,
                                                    tint = if (tx.type == "RECEIPT") AppColors.success else AppColors.error,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(14.dp))
                                            Column {
                                                Text(
                                                    tx.partyName ?: "Generic / Direct",
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 15.sp,
                                                    color = AppColors.textPrimary
                                                )
                                                Text(
                                                    "${Utils.formatDate(tx.date)}  ·  ${tx.mode}",
                                                    fontSize = 12.sp,
                                                    color = AppColors.textSecondary
                                                )
                                                if (!tx.narration.isNullOrBlank()) {
                                                    Text(tx.narration, fontSize = 12.sp, color = AppColors.textHint)
                                                }
                                            }
                                        }
                                        Text(
                                            "${if (tx.type == "RECEIPT") "+" else "-"}${Utils.formatIndianCurrency(tx.amount)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp,
                                            color = if (tx.type == "RECEIPT") AppColors.success else AppColors.error
                                        )
                                    }
                                }
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
private fun TabletAddTransactionForm(
    viewModel: AppViewModel,
    parties: List<Party>,
    partiesReadState: UiLoadState<List<Party>>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = LocalAppWindowSizeClass.current
    var type by remember { mutableStateOf("PAYMENT") }
    var mode by remember { mutableStateOf("CASH") }
    var amountStr by remember { mutableStateOf("") }
    var selectedParty by remember { mutableStateOf<Party?>(null) }
    var partyDropdownExpanded by remember { mutableStateOf(false) }
    var narration by remember { mutableStateOf("") }
    var chequeNo by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppColors.screenBg,
        topBar = {
            TopAppBar(
                title = { Text("Log Transaction", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.cardBg,
                    titleContentColor = AppColors.textPrimary
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = windowSizeClass.dashboardMaxWidthDp()?.dp ?: Dp.Unspecified)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { type = "PAYMENT" },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (type == "PAYMENT") AppColors.error.copy(alpha = 0.1f) else Color.Transparent
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (type == "PAYMENT") 2.dp else 1.dp,
                            color = if (type == "PAYMENT") AppColors.error else AppColors.border
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Money Out (Payment)", color = if (type == "PAYMENT") AppColors.error else AppColors.textSecondary)
                    }

                    OutlinedButton(
                        onClick = { type = "RECEIPT" },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (type == "RECEIPT") AppColors.success.copy(alpha = 0.1f) else Color.Transparent
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (type == "RECEIPT") 2.dp else 1.dp,
                            color = if (type == "RECEIPT") AppColors.success else AppColors.border
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Money In (Receipt)", color = if (type == "RECEIPT") AppColors.success else AppColors.textSecondary)
                    }
                }

                Text("Payment Method", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("CASH", "BANK", "UPI", "CHEQUE").forEach { m ->
                        FilterChip(
                            selected = mode == m,
                            onClick = { mode = m },
                            label = { Text(m) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = filterDecimalInput(it) },
                    label = { Text("Transaction Amount *") },
                    modifier = Modifier.fillMaxWidth().testTag("manual_tx_amount"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(8.dp)
                )

                UiLoadStateNotice(partiesReadState, viewModel::retryFailedReads)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedParty?.name ?: "No party (Direct Cash / Generic expense)",
                        onValueChange = {},
                        label = { Text("Linked Party Account") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier.clickable { partyDropdownExpanded = true }
                            )
                        }
                    )
                    DropdownMenu(
                        expanded = partyDropdownExpanded,
                        onDismissRequest = { partyDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f).height(240.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("None (Direct Transaction)") },
                            onClick = {
                                selectedParty = null
                                partyDropdownExpanded = false
                            }
                        )
                        parties.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.name) },
                                onClick = {
                                    selectedParty = p
                                    partyDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                if (mode == "CHEQUE") {
                    OutlinedTextField(
                        value = chequeNo,
                        onValueChange = { chequeNo = it },
                        label = { Text("Cheque / draft Number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = bankName,
                        onValueChange = { bankName = it },
                        label = { Text("Cleared Bank Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                OutlinedTextField(
                    value = narration,
                    onValueChange = { narration = it },
                    label = { Text("Memo Narration") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                if (showError) {
                    Text("Please fill in the amount with valid number values.", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        val amt = amountStr.toDoubleOrNull()
                        if (amt == null || amt <= 0) {
                            showError = true
                        } else {
                            val transactionObj = BankCashTransaction(
                                id = UUID.randomUUID().toString(),
                                type = type,
                                mode = mode,
                                amount = amt,
                                date = System.currentTimeMillis(),
                                partyId = selectedParty?.id,
                                partyName = selectedParty?.name ?: "Generic Account Direct",
                                sourceVoucherId = null,
                                narration = narration,
                                chequeNo = if (mode == "CHEQUE") chequeNo else null,
                                chequeDate = if (mode == "CHEQUE") System.currentTimeMillis() else null,
                                bankName = if (mode == "CHEQUE") bankName else null
                            )
                            viewModel.saveTransaction(transactionObj) {
                                onDismiss()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp).testTag("save_manual_tx_button"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.padding(end = 6.dp))
                    Text("Commit Transaction", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
