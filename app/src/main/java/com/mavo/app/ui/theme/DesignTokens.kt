package com.mavo.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object LayoutTokens {
    val paneGap = 16.dp
    val compactPaneGap = 12.dp
}

object Semantic {
    val success = Color(0xFF22A06B)
    val successBg = Color(0xFFE8F8F0)
    val error = Color(0xFFE24B4A)
    val errorBg = Color(0xFFFEF0F0)
    val warning = Color(0xFFD97706)
    val warningBg = Color(0xFFFFF7E6)
    val info = Color(0xFF6366F1)
    val infoBg = Color(0xFFEEF2FF)

    val debit = Color(0xFFE24B4A)
    val debitBg = Color(0xFFFEF0F0)
    val credit = Color(0xFF22A06B)
    val creditBg = Color(0xFFE8F8F0)

    val balancePositive = Color(0xFF22A06B)
    val balanceNegative = Color(0xFFE24B4A)
    val balanceNeutral = Color(0xFF1A1A1A)

    val chartIncome = Color(0xFF22A06B)
    val chartExpense = Color(0xFFE24B4A)
    val chartPending = Color(0xFFD97706)
    val chartNeutral = Color(0xFF9CA3AF)
}

object Surface {
    val background = Color(0xFFF7F7F7)
    val card = Color(0xFFFFFFFF)
    val cardBorder = Color(0xFFECECEC)
    val input = Color(0xFFFFFFFF)
    val inputBorder = Color(0xFFE5E5E5)
    val inputBorderFocus = Color(0xFF0A0A0A)
    val divider = Color(0xFFF0F0F0)
    val border = Color(0xFFE6E6E6)
    val borderLight = Color(0xFFF2F2F2)
    val shimmer = Color(0xFFEFEFEF)

    val sectionHeaderBg = Color(0xFFF2F2F2)
    val sectionHeaderText = Color(0xFF0F172A)
    val tableHeaderText = Color(0xFF64748B)
    val tableRowEven = Color(0xFFFFFFFF)
    val tableRowOdd = Color(0xFFFAFAFA)
}

object TextColors {
    val primary = Color(0xFF1A1A1A)
    val secondary = Color(0xFF4A4A4A)
    val tertiary = Color(0xFF888888)
    val onPrimary = Color(0xFFFFFFFF)
    val onDark = Color(0xFFFFFFFF)
    val disabled = Color(0xFFBBBBBB)
    val label = Color(0xFF6B7280)
}

object Badge {
    val saleBg = Color(0xFFE8F8F0)
    val saleText = Color(0xFF22A06B)
    val purchaseBg = Color(0xFFFEF0F0)
    val purchaseText = Color(0xFFE24B4A)
    val receiptBg = Color(0xFFFFF7E6)
    val receiptText = Color(0xFFD97706)
    val paymentBg = Color(0xFFEEF2FF)
    val paymentText = Color(0xFF6366F1)
    val returnBg = Color(0xFFFEF0F0)
    val returnText = Color(0xFFE24B4A)
    val overdueBg = Color(0xFFFEF0F0)
    val overdueText = Color(0xFFE24B4A)
    val partialBg = Color(0xFFFFF7E6)
    val partialText = Color(0xFFD97706)
    val paidBg = Color(0xFFE8F8F0)
    val paidText = Color(0xFF22A06B)
}

object ThemeNames {
    const val ZERO = "ZERO"
    const val SAFFRON = "SAFFRON"
    const val SLATE = "SLATE"
    const val INK = "INK"
    const val LEDGER = "LEDGER"
    const val NIGHT = "NIGHT"
}
