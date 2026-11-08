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

// 60:30:10 Mavo Loop Color Scheme
// 60% Peach Paper: #F6E0CF
// 30% Aubergine Ink: #3A263A
// 10% Mango Punch: #F2A83B
object BrandColors {
    val peachPaper = Color(0xFFF6E0CF)
    val peachSurface = Color(0xFFFAF0E6)
    val peachDarker = Color(0xFFEED6C2)
    val aubergineInk = Color(0xFF3A263A)
    val mangoPunch = Color(0xFFF2A83B)
}

// Accounting status colors remain separate utility tokens paired with text/icons
object Semantic {
    val success = Color(0xFF386641)
    val successBg = Color(0xFFE4EFE2)
    val error = Color(0xFF9E2A2B)
    val errorBg = Color(0xFFF9E8E8)
    val warning = Color(0xFFB5651D)
    val warningBg = Color(0xFFF9EFE6)
    val info = Color(0xFF5A485A)
    val infoBg = Color(0xFFECE3EC)

    val debit = Color(0xFF9E2A2B)
    val debitBg = Color(0xFFF9E8E8)
    val credit = Color(0xFF386641)
    val creditBg = Color(0xFFE4EFE2)

    val balancePositive = Color(0xFF386641)
    val balanceNegative = Color(0xFF9E2A2B)
    val balanceNeutral = Color(0xFF3A263A)

    val chartIncome = Color(0xFF386641)
    val chartExpense = Color(0xFF9E2A2B)
    val chartPending = Color(0xFFB5651D)
    val chartNeutral = Color(0xFF705B70)
}

object Surface {
    // 60% Soft Peach Surfaces
    val background = Color(0xFFF6E0CF)
    val paperBackground = Color(0xFFF6E0CF)
    val card = Color(0xFFFAF0E6)
    val cardBorder = Color(0xFFE5CCA8)
    val input = Color(0xFFFAF0E6)
    val inputBorder = Color(0xFFDEC3B1)
    val inputBorderFocus = Color(0xFFF2A83B)
    val divider = Color(0xFFE6CCA8)
    val border = Color(0xFFDFC4AF)
    val borderLight = Color(0xFFEED6C2)
    val shimmer = Color(0xFFE8D2C1)

    val sectionHeaderBg = Color(0xFFEED6C2)
    val sectionHeaderText = Color(0xFF3A263A)
    val tableHeaderText = Color(0xFF5E455E)
    val tableRowEven = Color(0xFFFAF0E6)
    val tableRowOdd = Color(0xFFF4DDD0)
}

object TextColors {
    // 30% Aubergine Ink Hierarchy
    val primary = Color(0xFF3A263A)      // Aubergine Ink (10.8:1 contrast on Peach)
    val secondary = Color(0xFF5E455E)    // Medium Aubergine
    val tertiary = Color(0xFF8A718A)     // Soft Aubergine
    val onPrimary = Color(0xFF3A263A)    // Never white on Mango! Aubergine on Mango 6.9:1
    val onDark = Color(0xFFFFFFFF)
    val disabled = Color(0xFFA693A6)
    val label = Color(0xFF5E455E)
}

// Retain CafePalette for backwards compat with components transitioning to BrandColors
object CafePalette {
    val creamBase = Color(0xFFF6E0CF)
    val cardSurface = Color(0xFFFAF0E6)
    val coffeeStructure = Color(0xFF3A263A)
    val espressoDeep = Color(0xFF3A263A)
    val caramelAccent = Color(0xFFF2A83B)
    val caramelGlow = Color(0xFFF2A83B)
    val latteParchment = Color(0xFFEED6C2)
}

object Badge {
    val saleBg = Color(0xFFE4EFE2)
    val saleText = Color(0xFF386641)
    val purchaseBg = Color(0xFFF9E8E8)
    val purchaseText = Color(0xFF9E2A2B)
    val receiptBg = Color(0xFFF9EFE6)
    val receiptText = Color(0xFFB5651D)
    val paymentBg = Color(0xFFECE3EC)
    val paymentText = Color(0xFF5E455E)
    val returnBg = Color(0xFFF9E8E8)
    val returnText = Color(0xFF9E2A2B)
    val overdueBg = Color(0xFFF9E8E8)
    val overdueText = Color(0xFF9E2A2B)
    val partialBg = Color(0xFFF9EFE6)
    val partialText = Color(0xFFB5651D)
    val paidBg = Color(0xFFE4EFE2)
    val paidText = Color(0xFF386641)
}

object ThemeNames {
    const val MAVO = "MAVO"
    const val ZERO = "MAVO"
    const val SAFFRON = "MAVO"
    const val SLATE = "MAVO"
    const val LEDGER = "MAVO"
    const val NIGHT = "MAVO"
}
