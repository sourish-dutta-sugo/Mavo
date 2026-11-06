package com.mavo.app.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.LocalAppWindowSizeClass
import com.mavo.app.ui.screens.mobile.MobileBankCashScreen
import com.mavo.app.ui.screens.tablet.TabletBankCashScreen
import com.mavo.app.ui.usesNavigationRail

/**
 * Dispatcher router for BankCashScreen.
 * Delegates to MobileBankCashScreen or TabletBankCashScreen according to device window size.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankCashScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = LocalAppWindowSizeClass.current
    val isTablet = windowSizeClass.usesNavigationRail()

    if (isTablet) {
        TabletBankCashScreen(
            viewModel = viewModel,
            onNavigateBack = onNavigateBack,
            modifier = modifier
        )
    } else {
        MobileBankCashScreen(
            viewModel = viewModel,
            onNavigateBack = onNavigateBack,
            modifier = modifier
        )
    }
}
