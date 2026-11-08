package com.mavo.app.ui.screens.tablet

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.mavo.app.R
import com.mavo.app.data.AppPreferences
import com.mavo.app.data.LedgerEntry
import com.mavo.app.data.Product
import com.mavo.app.data.Utils
import com.mavo.app.data.Voucher
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.DashboardViewModel
import com.mavo.app.ui.LocalAppWindowSizeClass
import com.mavo.app.ui.UiLoadState
import com.mavo.app.ui.animation.premiumClickable
import com.mavo.app.ui.components.ChoiceChipRow
import com.mavo.app.ui.components.CircleIconButton
import com.mavo.app.ui.components.ScreenBorder
import com.mavo.app.ui.components.SearchField
import com.mavo.app.ui.components.ZbCard
import com.mavo.app.ui.dashboardMaxWidthDp
import com.mavo.app.ui.screens.AnalyticsFilter
import com.mavo.app.ui.screens.ChartType
import com.mavo.app.ui.screens.DashboardBalanceSnapshot
import com.mavo.app.ui.screens.KpiDetails
import com.mavo.app.ui.theme.AppColors
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

/**
 * Full Tablet view for Dashboard screen.
 * Supports Portrait & Landscape modes: wider 24dp margins, max-width clamping,
 * side-by-side arrangement for Quick Actions and Activity feeds in Landscape.
 */
@Composable
fun TabletDashboardScreen(
    viewModel: AppViewModel,
    dashboardViewModel: DashboardViewModel,
    modifier: Modifier = Modifier,
    onQuickAction: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val windowWidthSizeClass = LocalAppWindowSizeClass.current

    val vouchersReadState by viewModel.vouchersUiState.collectAsState()
    val ledgerEntriesReadState by viewModel.ledgerEntriesUiState.collectAsState()
    val productsReadState by viewModel.productsUiState.collectAsState()
    val profileReadState by viewModel.profileUiState.collectAsState()
    val vouchers = when (val state = vouchersReadState) {
        is UiLoadState.Content -> state.value
        is UiLoadState.Empty -> state.value
        is UiLoadState.Error -> state.cachedValue.orEmpty()
        UiLoadState.Loading -> emptyList()
    }
    val ledgerEntries = when (val state = ledgerEntriesReadState) {
        is UiLoadState.Content -> state.value
        is UiLoadState.Empty -> state.value
        is UiLoadState.Error -> state.cachedValue.orEmpty()
        UiLoadState.Loading -> emptyList()
    }
    val products = when (val state = productsReadState) {
        is UiLoadState.Content -> state.value
        is UiLoadState.Empty -> state.value
        is UiLoadState.Error -> state.cachedValue.orEmpty()
        UiLoadState.Loading -> emptyList()
    }
    val profile = when (val state = profileReadState) {
        is UiLoadState.Content -> state.value
        is UiLoadState.Empty -> state.value
        is UiLoadState.Error -> state.cachedValue
        UiLoadState.Loading -> null
    }
    val readStates = listOf(
        vouchersReadState,
        ledgerEntriesReadState,
        productsReadState,
        profileReadState
    )
    val isInitialLoading = readStates.any { it == UiLoadState.Loading }
    val hasUncachedError = readStates.any {
        it is UiLoadState.Error && !it.hasCachedValue
    }
    val hasCachedError = readStates.any {
        it is UiLoadState.Error && it.hasCachedValue
    }
    val isVoucherDataUnavailable = when (val state = vouchersReadState) {
        UiLoadState.Loading -> true
        is UiLoadState.Error -> !state.hasCachedValue
        else -> false
    }
    val headerState by dashboardViewModel.headerState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showProgressTracker by remember { mutableStateOf(false) }
    var progressMetric by remember { mutableStateOf("Sales") }
    var progressPeriod by remember { mutableStateOf("Monthly") }
    var progressTarget by remember { mutableStateOf(200000.0) }
    var showProgressDetails by remember { mutableStateOf(false) }
    var selectedAnalyticsCard by remember { mutableStateOf<KpiDetails?>(null) }
    var analyticsFilterByCard by remember { mutableStateOf(mapOf<String, AnalyticsFilter>()) }
    var analyticsChartTypeByCard by remember { mutableStateOf(mapOf<String, ChartType>()) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val todayStart = calendar.timeInMillis

    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfMonth = calendar.timeInMillis

    val voucherSummary by remember(vouchers, todayStart, firstDayOfMonth) {
        derivedStateOf {
            var todaySales = 0.0
            var todayPurchases = 0.0
            var thisMonthSales = 0.0
            var monthlyTaxableSales = 0.0
            var monthlyTaxablePurchases = 0.0
            var gstValue = 0.0

            vouchers.forEach { voucher ->
                gstValue += voucher.cgst + voucher.sgst + voucher.igst
                when (voucher.type) {
                    "SALE" -> {
                        if (voucher.date >= todayStart) todaySales += voucher.netAmount
                        if (voucher.date >= firstDayOfMonth) {
                            thisMonthSales += voucher.netAmount
                            monthlyTaxableSales += voucher.taxableAmount
                        }
                    }
                    "PURCHASE" -> {
                        if (voucher.date >= todayStart) todayPurchases += voucher.netAmount
                        if (voucher.date >= firstDayOfMonth) {
                            monthlyTaxablePurchases += voucher.taxableAmount
                        }
                    }
                }
            }
            TabletDashboardVoucherSummary(
                todaySales = todaySales,
                todayPurchases = todayPurchases,
                thisMonthSales = thisMonthSales,
                netProfit = monthlyTaxableSales - monthlyTaxablePurchases,
                gstValue = gstValue
            )
        }
    }

    val balanceSnapshot by remember(ledgerEntries) {
        derivedStateOf {
            var cash = 0.0
            var bank = 0.0
            val partyBalances = mutableMapOf<String, Double>()
            ledgerEntries.forEach { entry ->
                val change = entry.debit - entry.credit
                when {
                    entry.accountHead == "Cash" -> cash += change
                    entry.accountHead == "Bank" -> bank += change
                    entry.accountHead.startsWith("Party:") -> {
                        partyBalances[entry.accountHead] = (partyBalances[entry.accountHead] ?: 0.0) + change
                    }
                }
            }
            var rec = 0.0
            var pay = 0.0
            partyBalances.values.forEach { netBalance ->
                if (netBalance > 0) rec += netBalance
                else if (netBalance < 0) pay += abs(netBalance)
            }
            DashboardBalanceSnapshot(cash, bank, rec, pay)
        }
    }

    LaunchedEffect(Unit) {
        showProgressTracker = AppPreferences.isProgressTrackerEnabled(context)
        progressMetric = AppPreferences.getProgressTrackerMetric(context)
        progressPeriod = AppPreferences.getProgressTrackerPeriod(context)
        progressTarget = AppPreferences.getProgressTrackerTarget(context).toDoubleOrNull() ?: 200000.0
    }

    val recentTransactions by remember(vouchers) {
        derivedStateOf { vouchers.sortedByDescending { it.date }.take(8) }
    }

    val searchResults by remember(searchQuery, vouchers, ledgerEntries, products) {
        derivedStateOf {
            if (searchQuery.isBlank()) TabletDashboardSearchResults()
            else {
                val q = searchQuery.lowercase()
                TabletDashboardSearchResults(
                    vouchers = vouchers.filter {
                        it.voucherNo.lowercase().contains(q) ||
                            (it.partyId?.lowercase()?.contains(q) == true) ||
                            it.type.lowercase().contains(q) ||
                            Utils.formatDate(it.date).lowercase().contains(q)
                    }.take(5),
                    ledgerEntries = ledgerEntries.filter {
                        it.id.lowercase().contains(q) ||
                            it.accountHead.lowercase().contains(q) ||
                            (it.narration?.lowercase()?.contains(q) == true) ||
                            Utils.formatDate(it.date).lowercase().contains(q)
                    }.take(5),
                    products = products.filter {
                        it.id.lowercase().contains(q) ||
                            it.name.lowercase().contains(q) ||
                            (it.hsnCode?.lowercase()?.contains(q) == true)
                    }.take(5)
                )
            }
        }
    }

    val showGstCard by remember(profile) { derivedStateOf { profile?.gstin?.isNotBlank() == true } }
    val inventoryValue by remember(products) {
        derivedStateOf { products.sumOf { it.currentStock * it.saleRate } }
    }

    BackHandler(enabled = searchQuery.isNotBlank()) { searchQuery = "" }
    BackHandler(enabled = selectedAnalyticsCard != null) { selectedAnalyticsCard = null }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .widthIn(max = windowWidthSizeClass.dashboardMaxWidthDp()?.dp ?: Dp.Unspecified)
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.TopCenter)
                .verticalScroll(scrollState)
        ) {
            // Header for Tablet (24dp padding)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_mavo_logo),
                        contentDescription = "Mavo Logo",
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "FY ${headerState.fyLabel}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.textPrimary
                        )
                        Text(
                            text = profile?.gstin?.takeIf { it.isNotBlank() }?.let { "GSTIN: $it" } ?: "Non-GST",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.textSecondary
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(AppColors.primary)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = AppColors.textOnPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Text(
                    text = headerState.businessName.ifBlank { "Mavo" },
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textPrimary
                )
            }

            // Main Content Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search vouchers, items, contacts",
                    shape = RoundedCornerShape(28.dp)
                )

                if (hasCachedError || hasUncachedError) {
                    TabletDashboardReadStateCard(
                        isLoading = false,
                        hasCachedValue = hasCachedError,
                        onRetry = viewModel::retryFailedReads
                    )
                }

                val cardList = buildList {
                    add(KpiDetails("Today's Sales", Utils.formatIndianCurrency(voucherSummary.todaySales), AppColors.success))
                    add(KpiDetails("Today's Purchases", Utils.formatIndianCurrency(voucherSummary.todayPurchases), AppColors.error))
                    add(KpiDetails("This Month's Sales", Utils.formatIndianCurrency(voucherSummary.thisMonthSales), AppColors.success))
                    add(KpiDetails("Net Profit (Est.)", Utils.formatIndianCurrency(voucherSummary.netProfit), if (voucherSummary.netProfit >= 0) AppColors.success else AppColors.error))
                    add(KpiDetails("Receivables", Utils.formatIndianCurrency(balanceSnapshot.outstandingReceivable), AppColors.warning))
                    add(KpiDetails("Payables", Utils.formatIndianCurrency(balanceSnapshot.outstandingPayable), AppColors.error))
                    add(KpiDetails("Cash Account", Utils.formatIndianCurrency(balanceSnapshot.cashBalance), Color(0xFF1A5C4B)))
                    add(KpiDetails("Bank & UPI", Utils.formatIndianCurrency(balanceSnapshot.bankBalance), Color(0xFF22755F)))
                    add(KpiDetails("Inventory", Utils.formatIndianCurrency(inventoryValue), AppColors.gold))
                    if (showGstCard) {
                        add(KpiDetails("GST", Utils.formatIndianCurrency(voucherSummary.gstValue), AppColors.success))
                    }
                }

                if (isInitialLoading || hasUncachedError) {
                    TabletDashboardReadStateCard(
                        isLoading = isInitialLoading,
                        hasCachedValue = false,
                        onRetry = viewModel::retryFailedReads
                    )
                } else {
                    TabletDashboardKpiRow(
                        cards = cardList,
                        onCardClick = { selectedAnalyticsCard = it }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Search Results
                if (searchQuery.isNotBlank()) {
                    val foundVouchers = searchResults.vouchers
                    val foundLedger = searchResults.ledgerEntries
                    val foundProducts = searchResults.products

                    if (foundVouchers.isEmpty() && foundLedger.isEmpty() && foundProducts.isEmpty()) {
                        Text("No results found.", fontSize = 12.sp, color = AppColors.textTertiary, modifier = Modifier.padding(vertical = 8.dp))
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                            colors = CardDefaults.cardColors(containerColor = AppColors.cardBg),
                            border = BorderStroke(1.dp, AppColors.border),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (foundVouchers.isNotEmpty()) {
                                    Text("Vouchers", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.primary)
                                    foundVouchers.forEach { v ->
                                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("${v.voucherNo} . ${v.type}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AppColors.textPrimary)
                                                Text("${v.partyId ?: "Cash"} . ${Utils.formatDate(v.date)}", fontSize = 11.sp, color = AppColors.textTertiary)
                                            }
                                            Text(Utils.formatIndianCurrency(v.netAmount), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.textPrimary)
                                        }
                                        HorizontalDivider(color = AppColors.divider)
                                    }
                                }
                                if (foundLedger.isNotEmpty()) {
                                    if (foundVouchers.isNotEmpty()) Spacer(modifier = Modifier.height(4.dp))
                                    Text("Ledger Entries", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.primary)
                                    foundLedger.forEach { l ->
                                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(l.accountHead, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AppColors.textPrimary)
                                                Text(l.narration ?: "No Narration", fontSize = 11.sp, color = AppColors.textTertiary)
                                            }
                                            val amt = if (l.debit > 0) "Dr. ${Utils.formatIndianCurrency(l.debit)}" else "Cr. ${Utils.formatIndianCurrency(l.credit)}"
                                            Text(amt, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (l.debit > 0) AppColors.debit else AppColors.credit)
                                        }
                                        HorizontalDivider(color = AppColors.divider)
                                    }
                                }
                                if (foundProducts.isNotEmpty()) {
                                    if (foundVouchers.isNotEmpty() || foundLedger.isNotEmpty()) Spacer(modifier = Modifier.height(4.dp))
                                    Text("Stock Items", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.primary)
                                    foundProducts.forEach { p ->
                                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(p.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AppColors.textPrimary)
                                                Text("HSN: ${p.hsnCode ?: "N/A"} . Stock: ${p.openingStock}", fontSize = 11.sp, color = AppColors.textTertiary)
                                            }
                                            Text(Utils.formatIndianCurrency(p.saleRate), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.textPrimary)
                                        }
                                        HorizontalDivider(color = AppColors.divider)
                                    }
                                }
                            }
                        }
                    }
                }

                if (searchQuery.isBlank()) {
                    // Landscape layout vs Portrait layout
                    if (isLandscape) {
                        // Landscape Two-Column Pane
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Left Column: Progress Tracker + Quick Access
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                if (showProgressTracker) {
                                    TabletProgressTrackerCard(
                                        metric = progressMetric,
                                        period = progressPeriod,
                                        target = progressTarget,
                                        profit = voucherSummary.netProfit,
                                        onClick = { showProgressDetails = true }
                                    )
                                }
                                TabletQuickAccessCard(onQuickAction = onQuickAction)
                            }

                            // Right Column: Recent Transactions
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                TabletRecentTransactionsSection(
                                    recentTransactions = recentTransactions,
                                    isVoucherDataUnavailable = isVoucherDataUnavailable,
                                    vouchersReadState = vouchersReadState,
                                    onRetry = viewModel::retryFailedReads,
                                    onSeeAll = { onQuickAction("VOUCHERS") }
                                )
                            }
                        }
                    } else {
                        // Portrait: Stacked layout with tablet sizing
                        if (showProgressTracker) {
                            TabletProgressTrackerCard(
                                metric = progressMetric,
                                period = progressPeriod,
                                target = progressTarget,
                                profit = voucherSummary.netProfit,
                                onClick = { showProgressDetails = true }
                            )
                        }

                        TabletQuickAccessCard(onQuickAction = onQuickAction)

                        TabletRecentTransactionsSection(
                            recentTransactions = recentTransactions,
                            isVoucherDataUnavailable = isVoucherDataUnavailable,
                            vouchersReadState = vouchersReadState,
                            onRetry = viewModel::retryFailedReads,
                            onSeeAll = { onQuickAction("VOUCHERS") }
                        )
                    }
                }
            }
        }

        // Analytics Popup for Tablet
        if (selectedAnalyticsCard != null) {
            TabletKpiAnalyticsPopup(
                card = selectedAnalyticsCard!!,
                vouchers = vouchers,
                products = products,
                ledgerEntries = ledgerEntries,
                analyticsFilter = analyticsFilterByCard[selectedAnalyticsCard?.title] ?: AnalyticsFilter.THIS_MONTH,
                chartType = analyticsChartTypeByCard[selectedAnalyticsCard?.title] ?: ChartType.LINE,
                onFilterChange = { analyticsFilterByCard = analyticsFilterByCard + (selectedAnalyticsCard?.title.orEmpty() to it) },
                onChartTypeChange = { analyticsChartTypeByCard = analyticsChartTypeByCard + (selectedAnalyticsCard?.title.orEmpty() to it) },
                onDismiss = { selectedAnalyticsCard = null }
            )
        }

        // Progress Details Dialog
        if (showProgressDetails) {
            AlertDialog(
                onDismissRequest = { showProgressDetails = false },
                confirmButton = {
                    TextButton(onClick = { showProgressDetails = false }) {
                        Text("Close", color = AppColors.primary)
                    }
                },
                title = { Text("Progress Analytics", fontWeight = FontWeight.Bold, color = AppColors.textPrimary) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Metric: $progressMetric", color = AppColors.textPrimary)
                        Text("Period: $progressPeriod", color = AppColors.textPrimary)
                        Text("Target: ${Utils.formatIndianCurrency(progressTarget)}", color = AppColors.textPrimary)
                        Text("Current: ${Utils.formatIndianCurrency(voucherSummary.netProfit)}", color = AppColors.textPrimary, fontWeight = FontWeight.Bold)
                        val percent = ((voucherSummary.netProfit / progressTarget.coerceAtLeast(1.0)) * 100.0).coerceIn(0.0, 100.0)
                        Text("Progress: ${String.format(Locale.US, "%.2f", percent)}%", color = AppColors.primary, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = AppColors.cardBg,
                textContentColor = AppColors.textPrimary,
                titleContentColor = AppColors.textPrimary
            )
        }
    }
}

@Composable
private fun TabletProgressTrackerCard(
    metric: String,
    period: String,
    target: Double,
    profit: Double,
    onClick: () -> Unit
) {
    val progressPercent = ((profit / target.coerceAtLeast(1.0)) * 100.0).coerceIn(0.0, 100.0)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .premiumClickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AppColors.cardBg),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, AppColors.border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$metric Progress",
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.textPrimary,
                        fontSize = 14.sp
                    )
                    Text(
                        "${String.format(Locale.US, "%.1f", progressPercent)}%",
                        fontWeight = FontWeight.Bold,
                        color = AppColors.primary,
                        fontSize = 14.sp
                    )
                }
                LinearProgressIndicator(
                    progress = { (progressPercent / 100f).toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = AppColors.primary,
                    trackColor = AppColors.primary.copy(alpha = 0.12f)
                )
                Text(
                    "$period target: ${Utils.formatIndianCurrency(target)}",
                    fontSize = 12.sp,
                    color = AppColors.textTertiary
                )
            }
        }
    }
}

@Composable
private fun TabletQuickAccessCard(onQuickAction: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.primary),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, AppColors.border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TabletHomeQuickAction("Products", Icons.Outlined.Inventory2, contentColor = AppColors.textOnPrimary) { onQuickAction("PRODUCTS") }
            TabletHomeQuickAction("Chart of Accounts", Icons.AutoMirrored.Outlined.MenuBook, contentColor = AppColors.textOnPrimary) { onQuickAction("LEDGER_BOOKS") }
            TabletHomeQuickAction("Party Master", Icons.Outlined.People, contentColor = AppColors.textOnPrimary) { onQuickAction("PARTIES") }
            TabletHomeQuickAction("Reports", Icons.AutoMirrored.Filled.Assignment, contentColor = AppColors.textOnPrimary) { onQuickAction("REPORTS") }
        }
    }
}

@Composable
private fun TabletRecentTransactionsSection(
    recentTransactions: List<Voucher>,
    isVoucherDataUnavailable: Boolean,
    vouchersReadState: UiLoadState<List<Voucher>>,
    onRetry: () -> Unit,
    onSeeAll: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Transactions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "See all",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.textSecondary,
                modifier = Modifier.clickable { onSeeAll() }
            )
        }

        if (isVoucherDataUnavailable) {
            TabletDashboardReadStateCard(
                isLoading = vouchersReadState == UiLoadState.Loading,
                hasCachedValue = false,
                onRetry = onRetry
            )
        } else if (recentTransactions.isEmpty()) {
            ZbCard {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No transactions yet.",
                        fontSize = 15.sp,
                        color = AppColors.textTertiary
                    )
                }
            }
        } else {
            ZbCard(contentPadding = 0) {
                recentTransactions.forEachIndexed { index, voucher ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = voucher.partyId ?: "Cash",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${tabletDashboardTypeLabel(voucher.type)} · " +
                                    "${voucher.voucherNo.ifBlank { "\u2014" }} · " +
                                    Utils.formatDate(voucher.date),
                                fontSize = 14.sp,
                                color = AppColors.textSecondary
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "${tabletDashboardAmountSign(voucher.type)}${Utils.formatIndianCurrency(voucher.netAmount)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.textPrimary
                        )
                    }
                    if (index < recentTransactions.lastIndex) {
                        HorizontalDivider(color = ScreenBorder, thickness = 1.dp)
                    }
                }
            }
        }
    }
}

@Immutable
private data class TabletDashboardVoucherSummary(
    val todaySales: Double = 0.0,
    val todayPurchases: Double = 0.0,
    val thisMonthSales: Double = 0.0,
    val netProfit: Double = 0.0,
    val gstValue: Double = 0.0
)

@Immutable
private data class TabletDashboardSearchResults(
    val vouchers: List<Voucher> = emptyList(),
    val ledgerEntries: List<LedgerEntry> = emptyList(),
    val products: List<Product> = emptyList()
)

@Composable
private fun TabletDashboardReadStateCard(
    isLoading: Boolean,
    hasCachedValue: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isLoading -> MaterialTheme.colorScheme.surfaceContainerLow
                hasCachedValue -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.errorContainer
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
            Text(
                modifier = Modifier.weight(1f),
                text = when {
                    isLoading -> "Loading your books…"
                    hasCachedValue -> "Showing saved data. Some records could not refresh."
                    else -> "Some records are unavailable. Retry to load them."
                },
                color = if (isLoading || hasCachedValue) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onErrorContainer
                },
                style = MaterialTheme.typography.bodyLarge
            )
            if (!isLoading) {
                TextButton(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
}

private fun tabletDashboardTypeLabel(type: String): String = when (type) {
    "SALE" -> "Sale"
    "PURCHASE" -> "Purchase"
    "RECEIPT" -> "Receipt"
    "PAYMENT" -> "Payment"
    "JOURNAL" -> "Journal"
    "INCOME" -> "Income"
    "EXPENSE" -> "Expense"
    else -> type.lowercase().replaceFirstChar { it.uppercase() }
}

private fun tabletDashboardAmountSign(type: String): String = when (type) {
    "SALE", "RECEIPT", "INCOME" -> "+ "
    "PURCHASE", "PAYMENT", "EXPENSE" -> "\u2212 "
    else -> ""
}

@Composable
private fun RowScope.TabletHomeQuickAction(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    contentColor: Color = AppColors.textPrimary,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .weight(1f)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TabletDashboardKpiRow(
    cards: List<KpiDetails>,
    onCardClick: (KpiDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    LazyRow(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(cards) { _, card ->
            TabletCompactKpiCard(
                details = card,
                onClick = { onCardClick(card) }
            )
        }
    }
}

@Composable
private fun TabletCompactKpiCard(
    details: KpiDetails,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(96.dp)
            .premiumClickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.cardBg),
        border = BorderStroke(1.dp, AppColors.border),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = details.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                ) {
                    val strokeWidth = 1.dp.toPx()
                    val dashLength = 4.dp.toPx()
                    val gapLength = 3.dp.toPx()
                    val pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(dashLength, gapLength), 0f)
                    drawLine(
                        color = AppColors.border,
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = strokeWidth,
                        pathEffect = pathEffect
                    )
                }
            }
            Text(
                text = details.amount,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TabletKpiAnalyticsPopup(
    card: KpiDetails,
    vouchers: List<Voucher>,
    products: List<Product>,
    ledgerEntries: List<LedgerEntry>,
    analyticsFilter: AnalyticsFilter,
    chartType: ChartType,
    onFilterChange: (AnalyticsFilter) -> Unit,
    onChartTypeChange: (ChartType) -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    val analyticsSeries = remember(card.title, analyticsFilter, vouchers, products, ledgerEntries) {
        computeTabletAnalyticsSeries(card.title, analyticsFilter, vouchers, products, ledgerEntries)
    }

    val currentValue = analyticsSeries.lastOrNull() ?: 0.0
    val previousValue = analyticsSeries.firstOrNull() ?: 0.0
    val growth = if (previousValue != 0.0) ((currentValue - previousValue) / previousValue) * 100.0 else 0.0
    val highestValue = analyticsSeries.maxOrNull() ?: 0.0
    val lowestValue = analyticsSeries.minOrNull() ?: 0.0
    val averageValue = if (analyticsSeries.isNotEmpty()) analyticsSeries.average() else 0.0

    val periods = listOf(
        AnalyticsFilter.TODAY to "Today",
        AnalyticsFilter.THIS_WEEK to "Week",
        AnalyticsFilter.THIS_MONTH to "Month",
        AnalyticsFilter.THIS_QUARTER to "Quarter",
        AnalyticsFilter.THIS_YEAR to "Year"
    )
    val periodIndex = periods.indexOfFirst { it.first == analyticsFilter }.let { if (it < 0) 2 else it }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .pointerInput(Unit) {
                detectTapGestures { onDismiss() }
            }
            .zIndex(100f),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxWidth(0.85f)
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.85f)
                .pointerInput(Unit) { detectTapGestures { } }
                .zIndex(101f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.screenBg),
            border = BorderStroke(1.dp, AppColors.border)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Metric", fontSize = 13.sp, color = AppColors.textTertiary)
                        Text(
                            card.title,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.textPrimary
                        )
                    }
                    CircleIconButton(
                        icon = Icons.Default.Close,
                        contentDescription = "Close",
                        onClick = onDismiss
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        Utils.formatIndianCurrency(currentValue),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary
                    )
                    Text(
                        "${if (growth >= 0) "+" else ""}${String.format(Locale.US, "%.1f", growth)}% vs ${analyticsFilter.label.lowercase()}",
                        fontSize = 16.sp,
                        color = AppColors.textSecondary
                    )
                }
                ChoiceChipRow(
                    options = periods.map { it.second },
                    selectedIndex = periodIndex,
                    onSelect = { onFilterChange(periods[it].first) }
                )

                ZbCard(contentPadding = 16) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                if (analyticsSeries.isNotEmpty() && chartType != ChartType.PIE) {
                                    val maxValue = (analyticsSeries.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
                                    val padding = 16f
                                    val chartWidth = size.width - padding * 2
                                    val chartHeight = size.height - padding * 2

                                    for (i in 0..4) {
                                        val y = padding + i * (chartHeight / 4f)
                                        drawLine(AppColors.divider, Offset(padding, y), Offset(size.width - padding, y), 1f)
                                    }

                                    when (chartType) {
                                        ChartType.LINE -> {
                                            val points = analyticsSeries.mapIndexed { index, value ->
                                                val x = padding + index * (chartWidth / (analyticsSeries.size - 1).coerceAtLeast(1).toFloat())
                                                val y = padding + chartHeight - (value / maxValue * chartHeight).toFloat()
                                                Offset(x, y)
                                            }
                                            if (points.size >= 2) {
                                                val path = androidx.compose.ui.graphics.Path().apply {
                                                    moveTo(points.first().x, size.height - padding)
                                                    points.forEach { lineTo(it.x, it.y) }
                                                    lineTo(points.last().x, size.height - padding)
                                                    close()
                                                }
                                                drawPath(
                                                    path,
                                                    brush = Brush.verticalGradient(
                                                        colors = listOf(card.highlight.copy(alpha = 0.2f), Color.Transparent)
                                                    )
                                                )
                                            }
                                            points.windowed(2).forEach { (start, end) ->
                                                drawLine(card.highlight, start, end, 3f)
                                            }
                                            points.forEach { point ->
                                                drawCircle(card.highlight, 6f, point)
                                                drawCircle(AppColors.cardBg, 3f, point)
                                            }
                                        }
                                        ChartType.BAR -> {
                                            val barWidth = chartWidth / analyticsSeries.size * 0.6f
                                            val gap = chartWidth / analyticsSeries.size * 0.4f
                                            analyticsSeries.forEachIndexed { index, value ->
                                                val barHeight = (value / maxValue * chartHeight).toFloat()
                                                val x = padding + index * (chartWidth / analyticsSeries.size) + gap / 2
                                                val y = padding + chartHeight - barHeight
                                                drawRoundRect(
                                                    color = card.highlight,
                                                    topLeft = Offset(x, y),
                                                    size = Size(barWidth, barHeight.coerceAtLeast(2f)),
                                                    cornerRadius = CornerRadius(6f, 6f)
                                                )
                                            }
                                        }
                                        ChartType.PIE -> {
                                            val total = analyticsSeries.sum().coerceAtLeast(1.0)
                                            val centerX = size.width / 2f
                                            val centerY = size.height / 2f
                                            val radius = minOf(centerX, centerY) - 20f
                                            var startAngle = -90f
                                            val colors = listOf(
                                                card.highlight,
                                                AppColors.success,
                                                AppColors.warning,
                                                AppColors.error,
                                                AppColors.gold,
                                                Color(0xFF1A5C4B)
                                            )
                                            analyticsSeries.forEachIndexed { index, value ->
                                                val sweep = (value / total * 360f).toFloat()
                                                drawArc(
                                                    color = colors[index % colors.size],
                                                    startAngle = startAngle,
                                                    sweepAngle = sweep,
                                                    useCenter = true,
                                                    topLeft = Offset(centerX - radius, centerY - radius),
                                                    size = Size(radius * 2, radius * 2)
                                                )
                                                startAngle += sweep
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ZbCard(modifier = Modifier.weight(1f), contentPadding = 12) {
                        TabletAnalyticsStat("Highest", Utils.formatIndianCurrency(highestValue))
                    }
                    ZbCard(modifier = Modifier.weight(1f), contentPadding = 12) {
                        TabletAnalyticsStat("Lowest", Utils.formatIndianCurrency(lowestValue))
                    }
                    ZbCard(modifier = Modifier.weight(1f), contentPadding = 12) {
                        TabletAnalyticsStat("Average", Utils.formatIndianCurrency(averageValue))
                    }
                }

                Text("Chart Type", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.textSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChartType.entries.forEach { type ->
                        FilterChip(
                            selected = chartType == type,
                            onClick = { onChartTypeChange(type) },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(
                                        imageVector = when (type) {
                                            ChartType.LINE -> Icons.AutoMirrored.Filled.ShowChart
                                            ChartType.BAR -> Icons.Default.BarChart
                                            ChartType.PIE -> Icons.Default.PieChart
                                        },
                                        contentDescription = type.name,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(type.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 12.sp)
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AppColors.primary.copy(alpha = 0.12f),
                                selectedLabelColor = AppColors.primary
                            )
                        )
                    }
                }
            }
        }
    }
}

private fun computeTabletAnalyticsSeries(
    title: String,
    filter: AnalyticsFilter,
    vouchers: List<Voucher>,
    products: List<Product>,
    ledgerEntries: List<LedgerEntry>
): List<Double> {
    val now = System.currentTimeMillis()
    val dayMs = 24 * 3600 * 1000L
    val (bucketCount, getBucketStart, getBucketEnd) = when (filter) {
        AnalyticsFilter.TODAY -> Triple(6,
            { i: Int -> now - (5 - i) * dayMs },
            { i: Int -> now - (5 - i - 1) * dayMs }
        )
        AnalyticsFilter.THIS_WEEK -> Triple(7,
            { i: Int ->
                val cal = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -(6 - i))
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }
                cal.timeInMillis
            },
            { i: Int ->
                val cal = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -(6 - i - 1))
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }
                cal.timeInMillis
            }
        )
        AnalyticsFilter.THIS_MONTH -> Triple(6,
            { i: Int ->
                Calendar.getInstance().apply {
                    add(Calendar.MONTH, -i)
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            },
            { i: Int ->
                Calendar.getInstance().apply {
                    add(Calendar.MONTH, -i)
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                    add(Calendar.MONTH, 1)
                }.timeInMillis
            }
        )
        AnalyticsFilter.THIS_QUARTER -> Triple(6,
            { i: Int ->
                Calendar.getInstance().apply {
                    set(Calendar.MONTH, (get(Calendar.MONTH) / 3) * 3 - i)
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            },
            { i: Int ->
                Calendar.getInstance().apply {
                    set(Calendar.MONTH, (get(Calendar.MONTH) / 3) * 3 - i)
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                    add(Calendar.MONTH, 3)
                }.timeInMillis
            }
        )
        AnalyticsFilter.THIS_YEAR -> Triple(6,
            { i: Int ->
                Calendar.getInstance().apply {
                    set(Calendar.MONTH, get(Calendar.MONTH) - i)
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }.timeInMillis
            },
            { i: Int ->
                Calendar.getInstance().apply {
                    set(Calendar.MONTH, get(Calendar.MONTH) - i)
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                    add(Calendar.MONTH, 1)
                }.timeInMillis
            }
        )
    }

    val buckets = mutableListOf<Double>()
    repeat(bucketCount) { index ->
        val bucketStart = getBucketStart(index)
        val bucketEnd = getBucketEnd(index)
        val value = when (title) {
            "Today's Sales" -> vouchers.filter { it.type == "SALE" && it.date in bucketStart until bucketEnd }.sumOf { it.netAmount }
            "Today's Purchases" -> vouchers.filter { it.type == "PURCHASE" && it.date in bucketStart until bucketEnd }.sumOf { it.netAmount }
            "This Month's Sales" -> vouchers.filter { it.type == "SALE" && it.date in bucketStart until bucketEnd }.sumOf { it.netAmount }
            "Net Profit (Est.)" -> vouchers.filter { it.type == "SALE" && it.date in bucketStart until bucketEnd }.sumOf { it.taxableAmount } -
                vouchers.filter { it.type == "PURCHASE" && it.date in bucketStart until bucketEnd }.sumOf { it.taxableAmount }
            "Receivables" -> vouchers.filter { it.type == "SALE" && it.date in bucketStart until bucketEnd }.sumOf { it.netAmount } * 0.35
            "Payables" -> vouchers.filter { it.type == "PURCHASE" && it.date in bucketStart until bucketEnd }.sumOf { it.netAmount } * 0.32
            "Cash Account" -> ledgerEntries.filter { it.accountHead == "Cash" && it.date in bucketStart until bucketEnd }.sumOf { it.debit - it.credit }
            "Bank & UPI" -> ledgerEntries.filter { it.accountHead == "Bank" && it.date in bucketStart until bucketEnd }.sumOf { it.debit - it.credit }
            "Inventory" -> products.filter { it.createdAt in bucketStart until bucketEnd }.sumOf { it.currentStock * it.saleRate }
            "GST" -> vouchers.filter { it.date in bucketStart until bucketEnd }.sumOf { it.cgst + it.sgst + it.igst }
            else -> 0.0
        }
        buckets.add(value)
    }
    return buckets.reversed()
}

@Composable
private fun TabletAnalyticsStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, fontSize = 13.sp, color = AppColors.textSecondary)
        Text(value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AppColors.textPrimary)
    }
}
