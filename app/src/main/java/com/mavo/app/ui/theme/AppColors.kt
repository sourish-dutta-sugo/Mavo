package com.mavo.app.ui.theme

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
    // Aubergine on Mango accent (6.9:1). Never white text on Mango!
    val textOnPrimary get() = Color(0xFF3A263A)
    val textOnDark = Color(0xFFFFFFFF)
    val textDisabled = Color(0xFFA693A6)

    val primary get() = theme.accentPrimary
    val primaryDark get() = Color(0xFFD68E28)
    val primaryLight get() = theme.accentLight
    val primaryText get() = theme.accentPrimary

    val border get() = theme.textTertiary.copy(alpha = 0.22f)
    val borderFocus get() = theme.accentPrimary
    val borderLight get() = theme.backgroundTertiary

    val debit = Semantic.debit
    val debitBg get() = Semantic.debitBg
    val credit = Semantic.credit
    val creditBg get() = Semantic.creditBg

    val gold = Color(0xFFF2A83B)
    val goldLight = Color(0xFFFADAA8)

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

    val success = Semantic.success
    val successBg get() = Semantic.successBg
    val error = Semantic.error
    val errorBg get() = Semantic.errorBg
    val warning = Semantic.warning
    val warningBg get() = Semantic.warningBg
    val info = Semantic.info
    val infoBg get() = Semantic.infoBg
}
