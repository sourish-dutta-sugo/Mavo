package com.mavo.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object LayoutTokens {
    val hairline = 1.dp
    val micro = 4.dp
    val small = 8.dp
    val medium = 12.dp
    val large = 16.dp
    val xl = 24.dp
    val xxl = 32.dp
    val minTouchTarget = 48.dp

    val paneGap = 16.dp
    val compactPaneGap = 12.dp
}

object BrandColors {
    val peachPaper = Color(0xFFF6E0CF)
    val aubergineInk = Color(0xFF3A263A)
    val aubergineBorder = Color(0xFF3A263A).copy(alpha = 0.25f)
    val aubergineCard = Color(0xFFFAF3EC)
    val mangoPunch = Color(0xFFF2A83B)
}

// Accounting status colors remain separate utility tokens paired with text/icons
object Semantic {
    val success = Color(0xFF16A34A)
    val successBg = Color(0xFFD8EBDD)
    val error = Color(0xFFDC2626)
    val errorBg = Color(0xFFF4D8D4)
    val warning = Color(0xFFD97706)
    val warningBg = Color(0xFFF8E4B7)
    val info = Color(0xFF2563EB)
    val infoBg = Color(0xFFE1E5F3)

    val debit = error
    val debitBg = errorBg
    val credit = success
    val creditBg = successBg

    val balancePositive = success
    val balanceNegative = error
    val balanceNeutral = BrandColors.aubergineInk

    val chartIncome = success
    val chartExpense = error
    val chartPending = warning
    val chartNeutral = Color(0xFF8C7360)
}

object Surface {
    val background = Color(0xFFF6E0CF)
    val paperBackground = Color(0xFFF6E0CF)
    val card = Color(0xFFFAF3EC)
    val cardBorder = Color(0xFF3A263A).copy(alpha = 0.2f)
    val input = Color(0xFFEDE0D4)
    val inputBorder = Color(0xFF3A263A).copy(alpha = 0.3f)
    val inputBorderFocus = Color(0xFFF2A83B)
    val divider = Color(0x333A263A)
    val border = Color(0xFF3A263A).copy(alpha = 0.2f)
    val borderLight = Color(0xFF3A263A).copy(alpha = 0.1f)
    val shimmer = Color(0xFFE8DACD)

    val sectionHeaderBg = Color(0xFF3A263A)
    val sectionHeaderText = Color(0xFF3E2B1E)
    val tableHeaderText = Color(0xFF6B5344)
    val tableRowEven = Color(0xFFF6E0CF)
    val tableRowOdd = Color(0xFFEFE2D4)
}

object TextColors {
    val primary = Color(0xFF3E2B1E)
    val secondary = Color(0xFF6B5344)
    val tertiary = Color(0xFF8C7360)
    val onPrimary = Color(0xFFF6E0CF)
    val onDark = Color(0xFFF6E0CF)
    val disabled = Color(0xFFAD9886)
    val label = Color(0xFF6B5344)
}

object CafePalette {
    val creamBase = Color(0xFFF6E0CF)
    val cardSurface = Color(0xFFFAF3EC)
    val coffeeStructure = Color(0xFF3A263A)
    val espressoDeep = Color(0xFF1C1B1F)
    val caramelAccent = Color(0xFFF2A83B)
    val caramelGlow = Color(0xFFF2A83B)
    val latteParchment = Color(0xFFEDE0D4)
}

object Badge {
    val saleBg = Semantic.successBg
    val saleText = Semantic.success
    val purchaseBg = Semantic.errorBg
    val purchaseText = Semantic.error
    val receiptBg = Semantic.warningBg
    val receiptText = Semantic.warning
    val paymentBg = Semantic.infoBg
    val paymentText = Semantic.info
    val returnBg = Semantic.errorBg
    val returnText = Semantic.error
    val overdueBg = Semantic.errorBg
    val overdueText = Semantic.error
    val partialBg = Semantic.warningBg
    val partialText = Semantic.warning
    val paidBg = Semantic.successBg
    val paidText = Semantic.success
}

object ThemeNames {
    const val MAVO = "MAVO"
    const val ZERO = "MAVO"
    const val SAFFRON = "MAVO"
    const val SLATE = "MAVO"
    const val LEDGER = "MAVO"
    const val NIGHT = "MAVO"
}
