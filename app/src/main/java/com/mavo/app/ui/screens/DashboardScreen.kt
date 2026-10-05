package com.mavo.app.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NorthEast
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.SouthWest
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.zIndex
import com.mavo.app.R
import com.mavo.app.data.AppPreferences
import com.mavo.app.data.LedgerEntry
import com.mavo.app.data.Utils
import com.mavo.app.data.Voucher
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.DashboardViewModel
import com.mavo.app.ui.animation.m3SpringPress
import com.mavo.app.ui.components.ChoiceChipRow
import com.mavo.app.ui.components.CircleIconButton
import com.mavo.app.ui.components.ScreenBorder
import com.mavo.app.ui.components.SearchField
import com.mavo.app.ui.components.ZbCard
import com.mavo.app.ui.animation.premiumClickable
import com.mavo.app.ui.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

@Immutable
private data class DashboardVoucherSummary(
    val todaySales: Double = 0.0,
    val todayPurchases: Double = 0.0,
    val thisMonthSales: Double = 0.0,
    val netProfit: Double = 0.0,
    val gstValue: Double = 0.0
)

@Immutable
private data class DashboardSearchResults(
    val vouchers: List<Voucher> = emptyList(),
    val ledgerEntries: List<LedgerEntry> = emptyList(),
    val products: List<com.mavo.app.data.Product> = emptyList()
)

@Immutable
data class DashboardBalanceSnapshot(
    val cashBalance: Double = 0.0,
    val bankBalance: Double = 0.0,
    val outstandingReceivable: Double = 0.0,
    val outstandingPayable: Double = 0.0
)

@Immutable
data class KpiDetails(
    val title: String,
    val amount: String,
    val subt: String,
    val highlight: Color,
    val icon: ImageVector? = null,
    val trendValue: Double = 0.0
)

enum class ChartType { LINE, BAR, PIE }
enum class AnalyticsFilter(val label: String) {
    TODAY("Today"), THIS_WEEK("This Week"), THIS_MONTH("This Month"),
    THIS_QUARTER("This Quarter"), THIS_YEAR("This Year"),
    CUSTOM_DATE("Custom Date"), CUSTOM_DATE_RANGE("Custom Date Range")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: AppViewModel,
    dashboardViewModel: DashboardViewModel,
    isDesktop: Boolean = false,
    onQuickAction: (String) -> Unit
) {
    val vouchers by viewModel.vouchers.collectAsState()
    val ledgerEntries by viewModel.ledgerEntries.collectAsState()
    val products by viewModel.products.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val headerState by dashboardViewModel.headerState.collectAsState()
    val kpiAnimationMode by dashboardViewModel.kpiAnimationMode.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showProgressTracker by remember { mutableStateOf(false) }
    var progressMetric by remember { mutableStateOf("Sales") }
    var progressPeriod by remember { mutableStateOf("Monthly") }
    var progressTarget by remember { mutableStateOf(200000.0) }
    var activeTransactionFilter by remember { mutableStateOf("All Transactions") }
    var activeTransactionSort by remember { mutableStateOf("Newest First") }
    var showProgressDetails by remember { mutableStateOf(false) }
    var selectedAnalyticsCard by remember { mutableStateOf<KpiDetails?>(null) }
    var analyticsFilterByCard by remember { mutableStateOf(mapOf<String, AnalyticsFilter>()) }
    var analyticsChartTypeByCard by remember { mutableStateOf(mapOf<String, ChartType>()) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val isTablet = configuration.screenWidthDp >= 600

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
            DashboardVoucherSummary(
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

    val lowStockProducts by remember(products) {
        derivedStateOf {
            products.filter { it.enableStockAlert && it.currentStock <= it.lowStockThreshold }
        }
    }

    LaunchedEffect(Unit) {
        showProgressTracker = AppPreferences.isProgressTrackerEnabled(context)
        progressMetric = AppPreferences.getProgressTrackerMetric(context)
        progressPeriod = AppPreferences.getProgressTrackerPeriod(context)
        progressTarget = AppPreferences.getProgressTrackerTarget(context).toDoubleOrNull() ?: 200000.0
    }

    val visibleTransactions by remember(vouchers, activeTransactionFilter, activeTransactionSort) {
        derivedStateOf {
            val filtered = when (activeTransactionFilter) {
                "Sales" -> vouchers.filter { it.type == "SALE" }
                "Purchase" -> vouchers.filter { it.type == "PURCHASE" }
                "Receipt" -> vouchers.filter { it.type == "RECEIPT" }
                "Payment" -> vouchers.filter { it.type == "PAYMENT" }
                "Income" -> vouchers.filter { it.type == "SALE" || it.type == "RECEIPT" }
                "Expense" -> vouchers.filter { it.type == "PURCHASE" || it.type == "PAYMENT" }
                "Receivable" -> vouchers.filter { (it.type == "SALE" || it.type == "RECEIPT") && it.outstandingAmount > 0 }
                "Payable" -> vouchers.filter { (it.type == "PURCHASE" || it.type == "PAYMENT") && it.outstandingAmount > 0 }
                "Due" -> vouchers.filter { it.outstandingAmount > 0 }
                "Cancelled" -> vouchers.filter { it.status == "DRAFT" }
                "Draft" -> vouchers.filter { it.status == "DRAFT" }
                "GST Transactions" -> vouchers.filter { it.cgst + it.sgst + it.igst > 0.0 }
                else -> vouchers
            }
            when (activeTransactionSort) {
                "Oldest First" -> filtered.sortedBy { it.date }
                "Amount (High -> Low)" -> filtered.sortedByDescending { it.netAmount }
                "Amount (Low -> High)" -> filtered.sortedBy { it.netAmount }
                "Voucher Number (Ascending)" -> filtered.sortedBy { it.voucherNo.ifBlank { it.type } }
                "Voucher Number (Descending)" -> filtered.sortedByDescending { it.voucherNo.ifBlank { it.type } }
                "Party Name (A -> Z)" -> filtered.sortedBy { it.partyId ?: "Cash" }
                "Party Name (Z -> A)" -> filtered.sortedByDescending { it.partyId ?: "Cash" }
                else -> filtered.sortedByDescending { it.date }
            }
        }
    }

    val recentTransactions by remember(visibleTransactions) {
        derivedStateOf { visibleTransactions.take(8) }
    }

    val searchResults by remember(searchQuery, vouchers, ledgerEntries, products) {
        derivedStateOf {
            if (searchQuery.isBlank()) DashboardSearchResults()
            else {
                val q = searchQuery.lowercase()
                DashboardSearchResults(
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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            // ========== HEADER ==========
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.cardBg)
                    .padding(horizontal = if (isTablet) 24.dp else 16.dp)
                    .padding(top = 4.dp, bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(AppColors.textPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MenuBook,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Mavo",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.textPrimary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(AppColors.sectionHeaderBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = AppColors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = headerState.businessName.ifBlank { "Mavo" },
                    fontSize = 31.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = profile?.gstin
                        ?.takeIf { it.isNotBlank() }
                        ?.let { "GSTIN: $it" }
                        ?: "Non-GST business",
                    fontSize = 16.sp,
                    color = AppColors.textSecondary
                )
                Text(
                    text = "Financial Year: FY ${headerState.fyLabel}",
                    fontSize = 16.sp,
                    color = AppColors.textSecondary
                )

                Spacer(modifier = Modifier.height(22.dp))

                val pendingDues = vouchers.count { it.outstandingAmount > 0 }
                val alertText = when {
                    lowStockProducts.isNotEmpty() ->
                        "${lowStockProducts.size} product(s) at or below stock threshold. Tap to review."
                    pendingDues > 0 ->
                        "$pendingDues voucher(s) with payment pending. Tap to review."
                    else -> "Books up to date for FY ${headerState.fyLabel}."
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppColors.screenBg)
                        .border(1.dp, ScreenBorder, RoundedCornerShape(14.dp))
                        .then(
                            when {
                                lowStockProducts.isNotEmpty() -> Modifier.clickable { onQuickAction("LOW_STOCK") }
                                pendingDues > 0 -> Modifier.clickable { onQuickAction("VOUCHERS") }
                                else -> Modifier
                            }
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(AppColors.textPrimary)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = alertText,
                        fontSize = 15.sp,
                        color = AppColors.textPrimary
                    )
                }
            }

            HorizontalDivider(color = ScreenBorder)

            // ========== CONTENT ==========
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.screenBg)
                    .padding(horizontal = if (isTablet) 24.dp else 16.dp)
                    .padding(top = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search vouchers, items, contacts"
                )

            // ========== KPI CARDS SECTION ==========
            val cardList = buildList {
                add(KpiDetails("Today's Sales", Utils.formatIndianCurrency(voucherSummary.todaySales), "vs yesterday", Color(0xFF22A06B), null, voucherSummary.todaySales))
                add(KpiDetails("Today's Purchases", Utils.formatIndianCurrency(voucherSummary.todayPurchases), "vs yesterday", Color(0xFFE24B4A), null, voucherSummary.todayPurchases))
                add(KpiDetails("This Month's Sales", Utils.formatIndianCurrency(voucherSummary.thisMonthSales), "running total", Color(0xFF22A06B), null, voucherSummary.thisMonthSales))
                add(KpiDetails("Net Profit (Est.)", Utils.formatIndianCurrency(voucherSummary.netProfit), if (voucherSummary.netProfit >= 0) "positive" else "negative", if (voucherSummary.netProfit >= 0) Color(0xFF22A06B) else Color(0xFFE24B4A), null, voucherSummary.netProfit))
                add(KpiDetails("Receivables", Utils.formatIndianCurrency(balanceSnapshot.outstandingReceivable), "amount due in", Color(0xFFD97706), null, balanceSnapshot.outstandingReceivable))
                add(KpiDetails("Payables", Utils.formatIndianCurrency(balanceSnapshot.outstandingPayable), "amount due out", Color(0xFF6366F1), null, balanceSnapshot.outstandingPayable))
                add(KpiDetails("Cash Account", Utils.formatIndianCurrency(balanceSnapshot.cashBalance), "current balance", Color(0xFF1A5C4B), null, balanceSnapshot.cashBalance))
                add(KpiDetails("Bank & UPI", Utils.formatIndianCurrency(balanceSnapshot.bankBalance), "current balance", Color(0xFF22755F), null, balanceSnapshot.bankBalance))
                add(KpiDetails("Inventory", Utils.formatIndianCurrency(inventoryValue), "stock value", Color(0xFFC8943A), null, inventoryValue))
                if (showGstCard) {
                    add(KpiDetails("GST", Utils.formatIndianCurrency(voucherSummary.gstValue), "total tax", Color(0xFF059669), null, voucherSummary.gstValue))
                }
            }

            when (kpiAnimationMode) {
                "STANDARD_HORIZONTAL" -> {
                    KpiStandardHorizontal(
                        cards = cardList,
                        isTablet = isTablet,
                        onCardClick = { selectedAnalyticsCard = it }
                    )
                }
                "SPOTLIGHT_CAROUSEL" -> {
                    KpiSpotlightCarousel(
                        cards = cardList,
                        isTablet = isTablet,
                        onCardClick = { selectedAnalyticsCard = it }
                    )
                }
                else -> {
                    KpiWalletStack(
                        cards = cardList,
                        isTablet = isTablet,
                        onCardClick = { selectedAnalyticsCard = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ========== BOTTOM SECTION ==========
            // Search Results Overlay
            if (searchQuery.isNotBlank()) {
                val foundVouchers = searchResults.vouchers
                val foundLedger = searchResults.ledgerEntries
                val foundProducts = searchResults.products

                if (foundVouchers.isEmpty() && foundLedger.isEmpty() && foundProducts.isEmpty()) {
                    Text("No results found.", fontSize = 12.sp, color = AppColors.textTertiary, modifier = Modifier.padding(vertical = 8.dp))
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(0.5.dp, AppColors.border),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (foundVouchers.isNotEmpty()) {
                                Text("Vouchers", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AppColors.primary)
                                foundVouchers.forEach { v ->
                                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("${v.voucherNo} . ${v.type}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AppColors.textPrimary)
                                            Text("${v.partyId ?: "Cash"} . ${Utils.formatDate(v.date)}", fontSize = 10.sp, color = AppColors.textTertiary)
                                        }
                                        Text(Utils.formatIndianCurrency(v.netAmount), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.textPrimary)
                                    }
                                    HorizontalDivider(color = AppColors.divider, thickness = 0.5.dp)
                                }
                            }
                            if (foundLedger.isNotEmpty()) {
                                if (foundVouchers.isNotEmpty()) Spacer(modifier = Modifier.height(4.dp))
                                Text("Ledger Entries", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF7C3AED))
                                foundLedger.forEach { l ->
                                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(l.accountHead, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AppColors.textPrimary)
                                            Text(l.narration ?: "No Narration", fontSize = 10.sp, color = AppColors.textTertiary)
                                        }
                                        val amt = if (l.debit > 0) "Dr. ${Utils.formatIndianCurrency(l.debit)}" else "Cr. ${Utils.formatIndianCurrency(l.credit)}"
                                        Text(amt, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (l.debit > 0) AppColors.debit else AppColors.credit)
                                    }
                                    HorizontalDivider(color = AppColors.divider, thickness = 0.5.dp)
                                }
                            }
                            if (foundProducts.isNotEmpty()) {
                                if (foundVouchers.isNotEmpty() || foundLedger.isNotEmpty()) Spacer(modifier = Modifier.height(4.dp))
                                Text("Stock Items", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AppColors.success)
                                foundProducts.forEach { p ->
                                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(p.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AppColors.textPrimary)
                                            Text("HSN: ${p.hsnCode ?: "N/A"} . Stock: ${p.openingStock}", fontSize = 10.sp, color = AppColors.textTertiary)
                                        }
                                        Text(Utils.formatIndianCurrency(p.saleRate), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.textPrimary)
                                    }
                                    HorizontalDivider(color = AppColors.divider, thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }
            }

            if (searchQuery.isBlank()) {
                // Progress Tracker
                if (showProgressTracker) {
                    val progressPercent by remember(voucherSummary.netProfit, progressTarget) {
                        derivedStateOf {
                            ((voucherSummary.netProfit / progressTarget.coerceAtLeast(1.0)) * 100.0).coerceIn(0.0, 100.0)
                        }
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .premiumClickable { showProgressDetails = true },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(50.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        border = BorderStroke(0.5.dp, AppColors.border)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "${progressMetric} Progress",
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppColors.textPrimary,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        "${String.format(Locale.US, "%.1f", progressPercent)}%",
                                        fontWeight = FontWeight.Bold,
                                        color = AppColors.primary,
                                        fontSize = 13.sp
                                    )
                                }
                                LinearProgressIndicator(
                                    progress = (progressPercent / 100f).toFloat(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(999.dp)),
                                    color = AppColors.primary,
                                    trackColor = AppColors.primary.copy(alpha = 0.12f)
                                )
                                Text(
                                    "${progressPeriod} target",
                                    fontSize = 11.sp,
                                    color = AppColors.textTertiary
                                )
                            }
                        }
                    }
                }

                // ========== QUICK ACTIONS ==========
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HomeQuickAction("Sale", Icons.AutoMirrored.Outlined.ReceiptLong) { onQuickAction("SALE") }
                    HomeQuickAction("Purchase", Icons.Outlined.ShoppingCart) { onQuickAction("PURCHASE") }
                    HomeQuickAction("Receipt", Icons.Outlined.SouthWest) { onQuickAction("RECEIPT") }
                    HomeQuickAction("Payment", Icons.Outlined.NorthEast) { onQuickAction("PAYMENT") }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HomeQuickAction("Quick Sale", Icons.Default.Bolt) { onQuickAction("QUICK_SALE") }
                    HomeQuickAction("Reports", Icons.AutoMirrored.Filled.Assignment) { onQuickAction("REPORTS") }
                    HomeQuickAction("Expenses", Icons.AutoMirrored.Filled.TrendingUp) { onQuickAction("EXPENSES") }
                }

                // ========== RECENT TRANSACTIONS ==========
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "See all",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.textSecondary,
                        modifier = Modifier.clickable { onQuickAction("VOUCHERS") }
                    )
                }

                if (recentTransactions.isEmpty()) {
                    ZbCard {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No transactions yet.",
                                fontSize = 14.sp,
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
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = voucher.partyId ?: "Cash",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AppColors.textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "${dashboardTypeLabel(voucher.type)} · " +
                                            "${voucher.voucherNo.ifBlank { "\u2014" }} · " +
                                            Utils.formatDate(voucher.date),
                                        fontSize = 14.sp,
                                        color = AppColors.textSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "${dashboardAmountSign(voucher.type)}${Utils.formatIndianCurrency(voucher.netAmount)}",
                                    fontSize = 17.sp,
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

        }

        // KPI Analytics Popup (centered modal overlay)
        if (selectedAnalyticsCard != null) {
            KpiAnalyticsPopup(
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

private fun dashboardTypeLabel(type: String): String = when (type) {
    "SALE" -> "Sale"
    "PURCHASE" -> "Purchase"
    "RECEIPT" -> "Receipt"
    "PAYMENT" -> "Payment"
    "JOURNAL" -> "Journal"
    "INCOME" -> "Income"
    "EXPENSE" -> "Expense"
    else -> type.lowercase().replaceFirstChar { it.uppercase() }
}

private fun dashboardAmountSign(type: String): String = when (type) {
    "SALE", "RECEIPT", "INCOME" -> "+ "
    "PURCHASE", "PAYMENT", "EXPENSE" -> "\u2212 "
    else -> ""
}

@Composable
private fun RowScope.HomeQuickAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = AppColors.textPrimary,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.textPrimary
        )
    }
}

// =================== KPI ANIMATION MODES ===================

@Composable
private fun KpiStandardHorizontal(
    cards: List<KpiDetails>,
    isTablet: Boolean,
    onCardClick: (KpiDetails) -> Unit
) {
    val listState = rememberLazyListState()
    val cardWidthDp = if (isTablet) 300.dp else 170.dp

    LazyRow(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(cards) { _, card ->
            KpiPremiumCard(
                details = card,
                modifier = Modifier.width(cardWidthDp),
                isTablet = isTablet,
                onClick = { onCardClick(card) }
            )
        }
    }
}

@Composable
private fun KpiWalletStack(
    cards: List<KpiDetails>,
    isTablet: Boolean,
    onCardClick: (KpiDetails) -> Unit
) {
    val listState = rememberLazyListState()
    val dotAlpha = remember { Animatable(1f) }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            delay(1500)
            dotAlpha.animateTo(0f, tween(400))
        } else {
            dotAlpha.snapTo(1f)
        }
    }

    val currentIndex by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val visible = info.visibleItemsInfo
            if (visible.isEmpty()) 0
            else {
                val center = (info.viewportStartOffset + info.viewportEndOffset) / 2
                visible.minByOrNull { kotlin.math.abs(it.offset + it.size / 2 - center) }?.index ?: 0
            }
        }
    }

    val cardWidthDp = if (isTablet) 320.dp else 260.dp
    val overlapDp = (-8).dp

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = if (isTablet) 48.dp else 32.dp),
            horizontalArrangement = Arrangement.spacedBy(overlapDp)
        ) {
            itemsIndexed(cards) { index, card ->
                val absOffset = kotlin.math.abs(
                    (listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }?.offset ?: 0) -
                        (listState.layoutInfo.viewportStartOffset + listState.layoutInfo.viewportEndOffset) / 2
                ).toFloat()
                val normalizedOffset = (absOffset / (cardWidthDp.value * LocalDensity.current.density)).coerceIn(0f, 2f)
                val scale = 1f - (normalizedOffset * 0.06f).coerceAtMost(0.1f)
                val alpha = 1f - (normalizedOffset * 0.4f).coerceAtMost(0.5f)

                KpiPremiumCard(
                    details = card,
                    modifier = Modifier
                        .width(cardWidthDp)
                        .graphicsLayer {
                            this.scaleX = scale
                            this.scaleY = scale
                            this.alpha = alpha
                        }
                        .zIndex(10f - normalizedOffset),
                    isTablet = isTablet,
                    onClick = { onCardClick(card) }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { alpha = dotAlpha.value },
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(cards.size) { index ->
                val isActive = index == currentIndex
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(if (isActive) 6.dp else 4.dp)
                        .width(if (isActive) 18.dp else 4.dp)
                        .background(
                            if (isActive) AppColors.primary else AppColors.border,
                            RoundedCornerShape(999.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun KpiSpotlightCarousel(
    cards: List<KpiDetails>,
    isTablet: Boolean,
    onCardClick: (KpiDetails) -> Unit
) {
    val listState = rememberLazyListState()
    val dotAlpha = remember { Animatable(1f) }
    val density = LocalDensity.current.density

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            delay(1500)
            dotAlpha.animateTo(0f, tween(400))
        } else {
            dotAlpha.snapTo(1f)
        }
    }

    val currentIndex by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val visible = info.visibleItemsInfo
            if (visible.isEmpty()) 0
            else {
                val center = (info.viewportStartOffset + info.viewportEndOffset) / 2
                visible.minByOrNull { kotlin.math.abs(it.offset + it.size / 2 - center) }?.index ?: 0
            }
        }
    }

    val cardWidthDp = if (isTablet) 320.dp else 260.dp

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = if (isTablet) 56.dp else 40.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(cards) { index, card ->
                val absOffset = kotlin.math.abs(
                    (listState.layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }?.offset ?: 0) -
                        (listState.layoutInfo.viewportStartOffset + listState.layoutInfo.viewportEndOffset) / 2
                ).toFloat()
                val normalizedOffset = (absOffset / (cardWidthDp.value * density)).coerceIn(0f, 2f)
                val scale = 1f - (normalizedOffset * 0.15f).coerceAtMost(0.2f)
                val alphaVal = 1f - (normalizedOffset * 0.5f).coerceAtMost(0.6f)
                val rotationY = normalizedOffset * -8f

                KpiPremiumCard(
                    details = card,
                    modifier = Modifier
                        .width(cardWidthDp)
                        .graphicsLayer {
                            this.scaleX = scale
                            this.scaleY = scale
                            this.alpha = alphaVal
                            this.rotationY = rotationY
                            this.cameraDistance = 12f * density
                        },
                    isTablet = isTablet,
                    onClick = { onCardClick(card) }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { alpha = dotAlpha.value },
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(cards.size) { index ->
                val isActive = index == currentIndex
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(if (isActive) 8.dp else 5.dp)
                        .background(
                            if (isActive) AppColors.primary else AppColors.border,
                            CircleShape
                        )
                )
            }
        }
    }
}

@Composable
private fun KpiPremiumCard(
    details: KpiDetails,
    modifier: Modifier = Modifier,
    isTablet: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isTablet) 120.dp else 100.dp)
            .premiumClickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.cardBg),
        border = BorderStroke(1.dp, ScreenBorder),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isTablet) 18.dp else 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = details.title,
                fontSize = if (isTablet) 15.sp else 14.sp,
                fontWeight = FontWeight.Normal,
                color = AppColors.textSecondary
            )
            Text(
                text = details.amount,
                fontSize = if (isTablet) 26.sp else 24.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// =================== KPI ANALYTICS POPUP ===================

@Composable
private fun KpiAnalyticsPopup(
    card: KpiDetails,
    vouchers: List<Voucher>,
    products: List<com.mavo.app.data.Product>,
    ledgerEntries: List<LedgerEntry>,
    analyticsFilter: AnalyticsFilter,
    chartType: ChartType,
    onFilterChange: (AnalyticsFilter) -> Unit,
    onChartTypeChange: (ChartType) -> Unit,
    onDismiss: () -> Unit
) {
    val analyticsSeries = remember(card.title, analyticsFilter, vouchers, products, ledgerEntries) {
        computeAnalyticsSeries(card.title, analyticsFilter, vouchers, products, ledgerEntries)
    }

    val currentValue = analyticsSeries.lastOrNull() ?: 0.0
    val previousValue = analyticsSeries.firstOrNull() ?: 0.0
    val growth = if (previousValue != 0.0) ((currentValue - previousValue) / previousValue) * 100.0 else 0.0
    val highestValue = analyticsSeries.maxOrNull() ?: 0.0
    val lowestValue = analyticsSeries.minOrNull() ?: 0.0
    val averageValue = if (analyticsSeries.isNotEmpty()) analyticsSeries.average() else 0.0

    // ponytail: mockup 11 is a bottom sheet; custom-date filters bucket like "Month" and had
    // no real picker behind the old dropdown, so the chip row falls back to Month for them.
    val periods = listOf(
        AnalyticsFilter.TODAY to "Today",
        AnalyticsFilter.THIS_WEEK to "Week",
        AnalyticsFilter.THIS_MONTH to "Month",
        AnalyticsFilter.THIS_QUARTER to "Quarter",
        AnalyticsFilter.THIS_YEAR to "Year"
    )
    val periodIndex = periods.indexOfFirst { it.first == analyticsFilter }.let { if (it < 0) 2 else it }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .pointerInput(Unit) {
                detectTapGestures { onDismiss() }
            }
            .zIndex(100f),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.92f)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(AppColors.screenBg)
                .navigationBarsPadding()
                .pointerInput(Unit) { detectTapGestures { } }
                .zIndex(101f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
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

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        Utils.formatIndianCurrency(currentValue),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AppColors.textPrimary
                    )
                    Text(
                        "${if (growth >= 0) "+" else ""}${String.format(Locale.US, "%.1f", growth)}% vs ${analyticsFilter.label.lowercase()}",
                        fontSize = 15.sp,
                        color = AppColors.textSecondary
                    )
                }
                ChoiceChipRow(
                    options = periods.map { it.second },
                    selectedIndex = periodIndex,
                    onSelect = { onFilterChange(periods[it].first) }
                )

                ZbCard(contentPadding = 12) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
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
                                        drawLine(Color(0xFFE5E7EB), Offset(padding, y), Offset(size.width - padding, y), 1f)
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
                                                drawCircle(card.highlight, 5f, point)
                                                drawCircle(Color.White, 3f, point)
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
                                                card.highlight.copy(alpha = 0.7f),
                                                card.highlight.copy(alpha = 0.5f),
                                                card.highlight.copy(alpha = 0.3f),
                                                Color(0xFFEA580C),
                                                Color(0xFF7C3AED)
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

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ZbCard(modifier = Modifier.weight(1f), contentPadding = 12) {
                        AnalyticsStat("Highest", Utils.formatIndianCurrency(highestValue))
                    }
                    ZbCard(modifier = Modifier.weight(1f), contentPadding = 12) {
                        AnalyticsStat("Lowest", Utils.formatIndianCurrency(lowestValue))
                    }
                    ZbCard(modifier = Modifier.weight(1f), contentPadding = 12) {
                        AnalyticsStat("Average", Utils.formatIndianCurrency(averageValue))
                    }
                }

                Text("Chart Type", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.textSecondary)
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
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(type.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 11.sp)
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

private fun computeAnalyticsSeries(
    title: String,
    filter: AnalyticsFilter,
    vouchers: List<Voucher>,
    products: List<com.mavo.app.data.Product>,
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
        AnalyticsFilter.CUSTOM_DATE, AnalyticsFilter.CUSTOM_DATE_RANGE -> Triple(6,
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
fun AnalyticsStat(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 12.sp, color = AppColors.textSecondary)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppColors.textPrimary)
    }
}

@Composable
fun TransactionSummaryPill(modifier: Modifier = Modifier, label: String, value: String) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(AppColors.screenBg, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, fontSize = 10.sp, color = AppColors.textTertiary)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.textPrimary)
        }
    }
}

fun deriveTransactionStatus(voucher: Voucher): String {
    return when {
        voucher.status == "DRAFT" -> "Cancelled"
        voucher.outstandingAmount <= 0.0 -> "Paid"
        voucher.outstandingAmount < voucher.netAmount -> "Partially Paid"
        else -> "Due"
    }
}

@Composable
fun QuickActionItem(
    label: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .premiumClickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(backgroundColor, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.textPrimary
        )
    }
}

@Composable
fun KpiCard(
    details: KpiDetails,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .border(
                1.dp,
                if (isSelected) AppColors.primary else AppColors.border,
                RoundedCornerShape(16.dp)
            )
            .premiumClickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = AppColors.cardBg)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = details.title.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.textTertiary,
                letterSpacing = 0.5.sp
            )
            Text(
                text = details.amount,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary
            )
        }
    }
}
