package com.mavo.app.ui.theme

import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.runtime.Composable

val TextDark @Composable get() = AppColors.textPrimary
val TextGray @Composable get() = AppColors.textSecondary
val TextHint @Composable get() = AppColors.textTertiary

@Composable
fun darkDropdownItemColors(): MenuItemColors = MenuDefaults.itemColors(
    textColor = AppColors.textPrimary,
    leadingIconColor = AppColors.textPrimary,
    trailingIconColor = AppColors.textPrimary,
    disabledTextColor = AppColors.textTertiary
)
