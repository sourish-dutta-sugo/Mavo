package com.mavo.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Spacing and gap design tokens for consistent component layouts.
 * Change values here to update spacing, gaps, and margins globally.
 */
object LayoutTokens {
    val hairline: Dp = 1.dp
    val micro: Dp = 4.dp
    val small: Dp = 8.dp
    val medium: Dp = 12.dp
    val large: Dp = 16.dp
    val xl: Dp = 24.dp
    val xxl: Dp = 32.dp
    val xxxl: Dp = 48.dp
    val minTouchTarget: Dp = 48.dp

    val paneGap: Dp = 16.dp
    val compactPaneGap: Dp = 12.dp

    val screenPadding: Dp = 16.dp
    val cardPadding: Dp = 16.dp
    val cardCompactPadding: Dp = 12.dp
    val cardGap: Dp = 12.dp
    val itemGap: Dp = 8.dp
    val iconGap: Dp = 8.dp
    val rowGap: Dp = 8.dp
    val sectionGap: Dp = 16.dp
    val dialogPadding: Dp = 24.dp
}

object SpacingTokens {
    val zero: Dp = 0.dp
    val micro: Dp = LayoutTokens.micro
    val small: Dp = LayoutTokens.small
    val medium: Dp = LayoutTokens.medium
    val large: Dp = LayoutTokens.large
    val xl: Dp = LayoutTokens.xl
    val xxl: Dp = LayoutTokens.xxl
    val xxxl: Dp = LayoutTokens.xxxl

    val screenPadding: Dp = LayoutTokens.screenPadding
    val cardPadding: Dp = LayoutTokens.cardPadding
    val cardGap: Dp = LayoutTokens.cardGap
    val itemGap: Dp = LayoutTokens.itemGap
    val iconGap: Dp = LayoutTokens.iconGap
}

/**
 * Corner radii and shape design tokens.
 * Single place to adjust rounding for cards, buttons, inputs, dialogs, sheets, badges.
 */
object ShapeTokens {
    val none: Dp = 0.dp
    val xs: Dp = 4.dp
    val small: Dp = 8.dp
    val medium: Dp = 12.dp
    val navItem: Dp = 14.dp
    val large: Dp = 16.dp
    val xl: Dp = 24.dp
    val xxl: Dp = 28.dp
    val pill: Dp = 999.dp

    val cardRadius: Dp = large
    val buttonRadius: Dp = medium
    val inputRadius: Dp = medium
    val chipRadius: Dp = small
    val badgeRadius: Dp = pill
    val dialogRadius: Dp = large
    val sheetRadius: Dp = xl
    val barRadius: Dp = xxl

    val card: Shape = RoundedCornerShape(cardRadius)
    val button: Shape = RoundedCornerShape(buttonRadius)
    val input: Shape = RoundedCornerShape(inputRadius)
    val chip: Shape = RoundedCornerShape(chipRadius)
    val badge: Shape = RoundedCornerShape(badgeRadius)
    val dialog: Shape = RoundedCornerShape(dialogRadius)
    val sheet: Shape = RoundedCornerShape(topStart = sheetRadius, topEnd = sheetRadius)
    val pillShape: Shape = RoundedCornerShape(pill)
}

/**
 * Icon size tokens across the app.
 */
object IconTokens {
    val micro: Dp = 12.dp
    val small: Dp = 16.dp
    val medium: Dp = 20.dp
    val standard: Dp = 24.dp
    val large: Dp = 32.dp
    val xl: Dp = 40.dp
    val xxl: Dp = 48.dp
    val hero: Dp = 64.dp
}

/**
 * Dimension tokens for heights, widths, borders, touch targets.
 */
object DimensionTokens {
    val borderThin: Dp = 1.dp
    val borderThick: Dp = 2.dp
    val buttonHeightSmall: Dp = 36.dp
    val buttonHeightMedium: Dp = 44.dp
    val buttonHeightLarge: Dp = 52.dp
    val buttonHeightHero: Dp = 56.dp
    val inputHeight: Dp = 52.dp
    val minTouchTarget: Dp = 48.dp
    val barHeight: Dp = 56.dp
    val railWidth: Dp = 88.dp
    val avatarSmall: Dp = 32.dp
    val avatarMedium: Dp = 40.dp
    val avatarLarge: Dp = 48.dp
    val fabSize: Dp = 52.dp
}

/**
 * Font size and line height design tokens for text hierarchy.
 */
object TextSizeTokens {
    val micro: TextUnit = 10.sp
    val caption: TextUnit = 11.sp
    val bodySmall: TextUnit = 12.sp
    val bodyMedium: TextUnit = 14.sp
    val bodyLarge: TextUnit = 16.sp
    val titleSmall: TextUnit = 14.sp
    val titleMedium: TextUnit = 16.sp
    val titleLarge: TextUnit = 18.sp
    val headlineSmall: TextUnit = 18.sp
    val headlineMedium: TextUnit = 20.sp
    val headlineLarge: TextUnit = 24.sp
    val displaySmall: TextUnit = 24.sp
    val displayMedium: TextUnit = 28.sp
    val displayLarge: TextUnit = 32.sp

    val microLineHeight: TextUnit = 14.sp
    val captionLineHeight: TextUnit = 16.sp
    val bodySmallLineHeight: TextUnit = 16.sp
    val bodyMediumLineHeight: TextUnit = 20.sp
    val bodyLargeLineHeight: TextUnit = 24.sp
    val titleSmallLineHeight: TextUnit = 20.sp
    val titleMediumLineHeight: TextUnit = 22.sp
    val titleLargeLineHeight: TextUnit = 28.sp
    val headlineSmallLineHeight: TextUnit = 24.sp
    val headlineMediumLineHeight: TextUnit = 28.sp
    val headlineLargeLineHeight: TextUnit = 32.sp
    val displaySmallLineHeight: TextUnit = 32.sp
    val displayMediumLineHeight: TextUnit = 36.sp
    val displayLargeLineHeight: TextUnit = 40.sp
}

/**
 * Unified Master Tokens object bundling all variable design tokens.
 */
object MavoTokens {
    val Spacing = SpacingTokens
    val Layout = LayoutTokens
    val Shapes = ShapeTokens
    val Icons = IconTokens
    val Dimensions = DimensionTokens
    val TextSizes = TextSizeTokens
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
