package com.mavo.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object LayoutTokens {
    val paneGap = 16.dp
    val compactPaneGap = 12.dp
}

// 60:30:10 Cafe Color Scheme
// 60% Dominant Background: Warm latte, soft paper, minimal eye-strain
// 30% Structural Medium: Rich roasted coffee bean, cocoa tones, espresso text
// 10% Accent Punch: Warm terracotta caramel glow
object Semantic {
    val success = Color(0xFF4E7756)
    val successBg = Color(0xFFE9F1E8)
    val error = Color(0xFFA64438)
    val errorBg = Color(0xFFFBEBEA)
    val warning = Color(0xFFC47B2B)
    val warningBg = Color(0xFFFDF3E7)
    val info = Color(0xFF6B655D)
    val infoBg = Color(0xFFEDE9E3)

    val debit = Color(0xFFA64438)
    val debitBg = Color(0xFFFBEBEA)
    val credit = Color(0xFF4E7756)
    val creditBg = Color(0xFFE9F1E8)

    val balancePositive = Color(0xFF4E7756)
    val balanceNegative = Color(0xFFA64438)
    val balanceNeutral = Color(0xFF2E1F16)

    val chartIncome = Color(0xFF4E7756)
    val chartExpense = Color(0xFFA64438)
    val chartPending = Color(0xFFC47B2B)
    val chartNeutral = Color(0xFF8F7B6E)
}

object Surface {
    // 60% Soft Warm Paper Surfaces
    val background = Color(0xFFFBF8F4)
    val paperBackground = Color(0xFFFBF8F4)
    val paperGrid = Color(0xFFF0E8DE)
    val card = Color(0xFFFFFDFB)
    val cardBorder = Color(0xFFEFE6DC)
    val input = Color(0xFFF6F1EA)
    val inputBorder = Color(0xFFE2D6C7)
    val inputBorderFocus = Color(0xFFC86D44)
    val divider = Color(0xFFEDE3D5)
    val border = Color(0xFFE5D9CC)
    val borderLight = Color(0xFFF5EEE6)
    val shimmer = Color(0xFFEDE4D7)

    // Folder binder styling
    val folderTabActive = Color(0xFFFFFDFB)
    val folderTabInactive = Color(0xFFEFE8DE)
    val folderBorder = Color(0xFFE0D2C3)

    val sectionHeaderBg = Color(0xFFF4ECE2)
    val sectionHeaderText = Color(0xFF2E1F16)
    val tableHeaderText = Color(0xFF6E5A4D)
    val tableRowEven = Color(0xFFFFFDFB)
    val tableRowOdd = Color(0xFFF9F5EF)
}

object TextColors {
    // 30% Espresso & Mocha Typographic Hierarchy
    val primary = Color(0xFF2E1F16)      // Deep Roasted Espresso
    val secondary = Color(0xFF6E5A4D)    // Warm Mocha
    val tertiary = Color(0xFF9E8B7E)     // Steamed Milk Froth Gray-Brown
    val onPrimary = Color(0xFFFFFFFF)
    val onDark = Color(0xFFFFFFFF)
    val disabled = Color(0xFFB5A79C)
    val label = Color(0xFF6E5A4D)
}

object CafePalette {
    // The Golden Ratio (60:30:10)
    val creamBase = Color(0xFFFBF8F4)         // 60%
    val cardSurface = Color(0xFFFFFDFB)
    val coffeeStructure = Color(0xFF5D4432)   // 30%
    val espressoDeep = Color(0xFF2E1F16)
    val caramelAccent = Color(0xFFC86D44)     // 10%
    val caramelGlow = Color(0xFFDF8053)
    val latteParchment = Color(0xFFF2EDE5)
}

object Badge {
    val saleBg = Color(0xFFE9F1E8)
    val saleText = Color(0xFF4E7756)
    val purchaseBg = Color(0xFFFBEBEA)
    val purchaseText = Color(0xFFA64438)
    val receiptBg = Color(0xFFFDF3E7)
    val receiptText = Color(0xFFC47B2B)
    val paymentBg = Color(0xFFF4ECE2)
    val paymentText = Color(0xFF6E5A4D)
    val returnBg = Color(0xFFFBEBEA)
    val returnText = Color(0xFFA64438)
    val overdueBg = Color(0xFFFBEBEA)
    val overdueText = Color(0xFFA64438)
    val partialBg = Color(0xFFFDF3E7)
    val partialText = Color(0xFFC47B2B)
    val paidBg = Color(0xFFE9F1E8)
    val paidText = Color(0xFF4E7756)
}

object ThemeNames {
    const val ZERO = "ZERO"
    const val SAFFRON = "SAFFRON"
    const val SLATE = "SLATE"
    const val LEDGER = "LEDGER"
    const val NIGHT = "NIGHT"
}
