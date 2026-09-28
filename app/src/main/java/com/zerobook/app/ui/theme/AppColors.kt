package com.zerobook.app.ui.theme

import androidx.compose.ui.graphics.Color

object AppColors {
    private val theme: AppTheme
        get() = ThemeRuntime.currentTheme.value

    val screenBg get() = theme.backgroundPrimary
    val cardBg get() = theme.backgroundSecondary
    val inputBg get() = theme.backgroundTertiary
    val sectionHeaderBg get() = theme.accentLight
    val tableHeaderBg get() = theme.accentLight
    val tableRowEven get() = theme.backgroundSecondary
    val tableRowOdd get() = theme.backgroundPrimary
    val bottomBarBg get() = theme.backgroundSecondary
    val topBarBg get() = theme.backgroundSecondary
    val divider get() = theme.textTertiary.copy(alpha = 0.22f)
    val shimmerBg get() = theme.accentLight.copy(alpha = 0.45f)

    val textPrimary get() = theme.textPrimary
    val textSecondary get() = theme.textSecondary
    val textTertiary get() = theme.textTertiary
    val textHint get() = theme.textTertiary
    val textOnPrimary = Color(0xFFFFFFFF)
    val textOnDark = Color(0xFFFFFFFF)
    val textDisabled = Color(0xFFB0B0B0)

    val primary get() = theme.accentPrimary
    val primaryDark get() = theme.accentPrimary.copy(alpha = 0.85f)
    val primaryLight get() = theme.accentLight
    val primaryText get() = theme.accentPrimary

    val border get() = theme.textTertiary.copy(alpha = if (theme.isDark) 0.32f else 0.22f)
    val borderFocus get() = theme.accentPrimary
    val borderLight get() = theme.backgroundTertiary

    val debit = Color(0xFFE24B4A)
    val debitBg get() = if (theme.isDark) Color(0xFF452522) else Color(0xFFFEF0F0)
    val credit = Color(0xFF22A06B)
    val creditBg get() = if (theme.isDark) Color(0xFF1D4034) else Color(0xFFE8F8F0)

    val gold = Color(0xFFC8943A)
    val goldLight = Color(0xFFF0C060)

    val badgeSaleBg = Badge.saleBg
    val badgeSaleText = Badge.saleText
    val badgePurchaseBg = Badge.purchaseBg
    val badgePurchaseText = Badge.purchaseText
    val badgeReceiptBg = Badge.receiptBg
    val badgeReceiptText = Badge.receiptText
    val badgePaymentBg = Badge.paymentBg
    val badgePaymentText = Badge.paymentText
    val badgeReturnBg = Badge.returnBg
    val badgeReturnText = Badge.returnText
    val badgeOverdueBg = Badge.overdueBg
    val badgeOverdueText = Badge.overdueText
    val badgePartialBg = Badge.partialBg
    val badgePartialText = Badge.partialText
    val badgePaidBg = Badge.paidBg
    val badgePaidText = Badge.paidText

    val inputText get() = theme.textPrimary
    val inputPlaceholder get() = theme.textTertiary
    val inputBorder get() = border
    val inputBorderFocus get() = theme.accentPrimary
    val labelText get() = theme.textSecondary

    val success = Color(0xFF22A06B)
    val successBg get() = if (theme.isDark) Color(0xFF1D4034) else Color(0xFFE8F8F0)
    val error = Color(0xFFE24B4A)
    val errorBg get() = if (theme.isDark) Color(0xFF452522) else Color(0xFFFEF0F0)
    val warning = Color(0xFFD97706)
    val warningBg get() = if (theme.isDark) Color(0xFF49371D) else Color(0xFFFFF7E6)
    val info = Color(0xFF8B95FF)
    val infoBg get() = if (theme.isDark) Color(0xFF29305A) else Color(0xFFEEF2FF)
}
