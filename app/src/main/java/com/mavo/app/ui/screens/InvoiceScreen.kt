package com.mavo.app.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.LocalAppWindowSizeClass
import com.mavo.app.ui.screens.mobile.MobileInvoiceScreen
import com.mavo.app.ui.screens.tablet.TabletInvoiceScreen
import com.mavo.app.ui.usesNavigationRail

/**
 * Dispatcher router for InvoiceScreen.
 * Delegates to MobileInvoiceScreen or TabletInvoiceScreen according to device window size.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceScreen(
    viewModel: AppViewModel,
    voucherId: String,
    onNavigateBack: () -> Unit,
    onEditVoucher: (String) -> Unit,
    onCreateSaleFromVoucher: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val windowWidthSizeClass = LocalAppWindowSizeClass.current
    val isTablet = windowWidthSizeClass.usesNavigationRail()

    if (isTablet) {
        TabletInvoiceScreen(
            viewModel = viewModel,
            voucherId = voucherId,
            onNavigateBack = onNavigateBack,
            onEditVoucher = onEditVoucher,
            onCreateSaleFromVoucher = onCreateSaleFromVoucher,
            modifier = modifier
        )
    } else {
        MobileInvoiceScreen(
            viewModel = viewModel,
            voucherId = voucherId,
            onNavigateBack = onNavigateBack,
            onEditVoucher = onEditVoucher,
            onCreateSaleFromVoucher = onCreateSaleFromVoucher,
            modifier = modifier
        )
    }
}
