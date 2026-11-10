package com.mavo.app.ui.theme

import androidx.compose.ui.graphics.Color

object AppColors {
    private val theme: AppTheme
        get() = ThemeRuntime.currentTheme.value

    val screenBg get() = theme.backgroundPrimary
    val cardBg get() = theme.backgroundSecondary
    val inputBg get() = theme.backgroundTertiary
    val sectionHeaderBg get() = Surface.sectionHeaderBg
    val tableHeaderBg get() = Surface.sectionHeaderBg
    val tableRowEven get() = theme.backgroundSecondary
    val tableRowOdd get() = Surface.tableRowOdd
    val bottomBarBg get() = theme.backgroundSecondary
    val topBarBg get() = theme.backgroundSecondary
    val divider get() = Surface.divider
    val shimmerBg get() = Surface.shimmer

    val textPrimary get() = theme.textPrimary
    val textSecondary get() = theme.textSecondary
    val textTertiary get() = theme.textTertiary
    val textHint get() = theme.textTertiary
    val textOnPrimary get() = BrandColors.aubergineInk
    val textOnDark = BrandColors.peachPaper
    val textDisabled = Color(0xFF8D7D86)

    val primary get() = theme.accentPrimary
    val primaryDark get() = Color(0xFFB86F13)
    val primaryLight get() = theme.accentLight
    val primaryText get() = theme.accentPrimary

    val border get() = Surface.border
    val borderFocus get() = theme.accentPrimary
    val borderLight get() = Surface.borderLight

    val debit = Semantic.debit
    val debitBg get() = Semantic.debitBg
    val credit = Semantic.credit
    val creditBg get() = Semantic.creditBg

    val gold = Color(0xFFF2A83B)
    val goldLight = Color(0xFFFFDFA9)

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
