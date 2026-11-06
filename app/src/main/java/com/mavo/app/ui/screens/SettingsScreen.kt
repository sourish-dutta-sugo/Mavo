package com.mavo.app.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.LocalAppWindowSizeClass
import com.mavo.app.ui.screens.mobile.MobileSettingsScreen
import com.mavo.app.ui.screens.tablet.TabletSettingsScreen
import com.mavo.app.ui.theme.ThemeViewModel
import com.mavo.app.ui.usesNavigationRail

/**
 * Dispatcher router for SettingsScreen.
 * Delegates to MobileSettingsScreen or TabletSettingsScreen based on device window size.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: AppViewModel,
    themeViewModel: ThemeViewModel,
    isDesktop: Boolean = false,
    onNavigateBack: () -> Unit
) {
    val windowSizeClass = LocalAppWindowSizeClass.current
    val isTablet = windowSizeClass.usesNavigationRail() || isDesktop

    if (isTablet) {
        TabletSettingsScreen(
            viewModel = viewModel,
            themeViewModel = themeViewModel,
            isDesktop = isDesktop,
            onNavigateBack = onNavigateBack
        )
    } else {
        MobileSettingsScreen(
            viewModel = viewModel,
            themeViewModel = themeViewModel,
            isDesktop = isDesktop,
            onNavigateBack = onNavigateBack
        )
    }
}
