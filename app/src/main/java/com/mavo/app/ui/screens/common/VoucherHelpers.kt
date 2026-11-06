package com.mavo.app.ui.screens.common

import androidx.compose.ui.graphics.Color
import com.mavo.app.data.Utils
import com.mavo.app.data.Voucher
import com.mavo.app.ui.theme.AppColors
import java.util.Locale

val defaultIncomeCategories = listOf(
    "Sales Commission", "Interest Received", "Rental Income", "Freelance Services",
    "Consulting Fees", "Investment Returns", "Grants", "Donations",
    "Refunds Received", "Other Income"
)

val defaultExpenseCategories = listOf(
    "Rent", "Electricity", "Water", "Telephone", "Internet",
    "Staff Salary", "Transport", "Packaging", "Maintenance",
    "Office Supplies", "Advertising", "Insurance", "Bank Charges",
    "Miscellaneous", "Other"
)

val incomeCategories: List<String> get() = defaultIncomeCategories
val expenseCategories: List<String> get() = defaultExpenseCategories

data class VoucherTypeCardData(
    val key: String,
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val accent: Color
)

data class VoucherTabGroup(
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val types: List<VoucherTypeCardData>
)

data class VoucherFilterState(
    val type: String? = null,
    val paymentStatus: String? = null,
    val partyName: String? = null,
    val minAmount: Double? = null,
    val maxAmount: Double? = null,
    val reference: String? = null,
    val startDate: Long? = null,
    val endDate: Long? = null
) {
    val activeFilterCount: Int
        get() = buildList {
            if (!type.isNullOrBlank() && type != "ALL") add("type")
            if (!paymentStatus.isNullOrBlank() && paymentStatus != "ANY") add("paymentStatus")
            if (!partyName.isNullOrBlank()) add("partyName")
            if (minAmount != null && minAmount > 0.0) add("minAmount")
            if (maxAmount != null && maxAmount > 0.0) add("maxAmount")
            if (!reference.isNullOrBlank()) add("reference")
            if (startDate != null || endDate != null) add("dateRange")
        }.size
}

data class VoucherSortOption(val value: String, val label: String)
data class VoucherFilterChip(val key: String, val label: String)

fun voucherTypeLabel(type: String): String = when (type) {
    "SALE" -> "Tax Invoice"
    "PURCHASE" -> "Purchase Invoice"
    "RECEIPT" -> "Receipt"
    "PAYMENT" -> "Payment"
    "JOURNAL" -> "Journal"
    "SALE_RETURN" -> "Sales Return"
    "PURCHASE_RETURN" -> "Purchase Return"
    "BILLS_RECEIVABLE" -> "Bills Receivable"
    "BILLS_PAYABLE" -> "Bills Payable"
    "DEBIT_NOTE" -> "Debit Note"
    "CREDIT_NOTE" -> "Credit Note"
    "QUOTATION" -> "Quotation"
    "DELIVERY_CHALLAN" -> "Delivery Challan"
    "INCOME" -> "Income"
    "EXPENSE" -> "Expense"
    "INQUIRY" -> "Inquiry / RFQ"
    "SALES_ORDER" -> "Sales Order"
    "PURCHASE_ORDER" -> "Purchase Order"
    "GOODS_RECEIPT_NOTE" -> "Goods Receipt Note"
    "MATERIAL_NOTE" -> "Material In/Out"
    "REJECTION_NOTE" -> "Rejection In/Out"
    "PETTY_CASH" -> "Petty Cash"
    "PROFORMA" -> "Proforma Invoice"
    else -> type.replace('_', ' ').replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun voucherBadgeColor(type: String): Color = when (type) {
    "SALE", "RECEIPT", "INCOME", "BILLS_RECEIVABLE" -> AppColors.success
    "PURCHASE", "PAYMENT", "EXPENSE", "BILLS_PAYABLE" -> AppColors.error
    "QUOTATION", "SALES_ORDER", "PURCHASE_ORDER", "PROFORMA", "INQUIRY" -> AppColors.warning
    "SALE_RETURN", "PURCHASE_RETURN", "CREDIT_NOTE", "DEBIT_NOTE", "REJECTION_NOTE", "JOURNAL" -> Color(0xFF0F766E)
    "DELIVERY_CHALLAN", "GOODS_RECEIPT_NOTE", "MATERIAL_NOTE" -> AppColors.badgePaymentText
    "PETTY_CASH" -> AppColors.gold
    else -> AppColors.primary
}

fun applyVoucherFilters(
    vouchers: List<Voucher>,
    searchQuery: String,
    filterState: VoucherFilterState,
    partyNameById: Map<String, String>
): List<Voucher> {
    val normalizedQuery = searchQuery.trim()
    return vouchers.filter { voucher ->
        val partyName = voucher.partyId?.let { partyNameById[it] } ?: "Cash / Bank"
        val matchesSearch = normalizedQuery.isBlank() ||
            voucher.voucherNo.contains(normalizedQuery, ignoreCase = true) ||
            partyName.contains(normalizedQuery, ignoreCase = true) ||
            voucher.referenceNo.contains(normalizedQuery, ignoreCase = true) ||
            voucher.narration.contains(normalizedQuery, ignoreCase = true)

        val matchesType = when (filterState.type) {
            null, "", "ALL" -> true
            "INCOME" -> voucher.type in setOf("INCOME", "SALE", "RECEIPT")
            "EXPENSE" -> voucher.type in setOf("EXPENSE", "PURCHASE", "PAYMENT")
            else -> voucher.type == filterState.type
        }

        val matchesPaymentStatus = when (filterState.paymentStatus) {
            null, "", "ANY" -> true
            "PAID" -> voucher.outstandingAmount <= 0.0
            "PARTIAL" -> voucher.outstandingAmount in 0.0..<voucher.netAmount
            "UNPAID" -> voucher.outstandingAmount > 0.0
            else -> true
        }

        val matchesParty = filterState.partyName.isNullOrBlank() ||
            partyName.contains(filterState.partyName.trim(), ignoreCase = true)

        val matchesMinAmount = filterState.minAmount == null || voucher.netAmount >= filterState.minAmount
        val matchesMaxAmount = filterState.maxAmount == null || voucher.netAmount <= filterState.maxAmount
        val matchesReference = filterState.reference.isNullOrBlank() ||
            voucher.referenceNo.contains(filterState.reference.trim(), ignoreCase = true)
        val matchesDateRange = (filterState.startDate == null || voucher.date >= filterState.startDate) &&
            (filterState.endDate == null || voucher.date <= filterState.endDate)

        matchesSearch && matchesType && matchesPaymentStatus && matchesParty &&
            matchesMinAmount && matchesMaxAmount && matchesReference && matchesDateRange
    }
}

fun applyVoucherSorting(
    vouchers: List<Voucher>,
    sortOption: String,
    partyNameById: Map<String, String>
): List<Voucher> = when (sortOption) {
    "NEWEST_FIRST" -> vouchers.sortedByDescending { it.date }
    "OLDEST_FIRST" -> vouchers.sortedBy { it.date }
    "HIGHEST_AMOUNT" -> vouchers.sortedByDescending { it.netAmount }
    "LOWEST_AMOUNT" -> vouchers.sortedBy { it.netAmount }
    "VOUCHER_NUMBER" -> vouchers.sortedBy { it.voucherNo.lowercase(Locale.getDefault()) }
    "PARTY_NAME_AZ" -> vouchers.sortedBy { (it.partyId?.let { partyId -> partyNameById[partyId] } ?: "Cash / Bank").lowercase(Locale.getDefault()) }
    "PARTY_NAME_ZA" -> vouchers.sortedByDescending { (it.partyId?.let { partyId -> partyNameById[partyId] } ?: "Cash / Bank").lowercase(Locale.getDefault()) }
    else -> vouchers
}

