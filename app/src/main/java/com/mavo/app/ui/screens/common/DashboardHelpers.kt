package com.mavo.app.ui.screens.common

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.mavo.app.data.LedgerEntry
import com.mavo.app.data.Product
import com.mavo.app.data.Voucher
import java.util.Calendar

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
    val highlight: Color
)

enum class ChartType { LINE, BAR, PIE }

enum class AnalyticsFilter(val label: String) {
    TODAY("Today"),
    THIS_WEEK("This Week"),
    THIS_MONTH("This Month"),
    THIS_QUARTER("This Quarter"),
    THIS_YEAR("This Year"),
}

fun computeDashboardAnalyticsSeries(
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

