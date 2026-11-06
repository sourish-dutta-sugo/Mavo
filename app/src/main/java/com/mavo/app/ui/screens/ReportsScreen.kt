package com.mavo.app.ui.screens

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mavo.app.data.BusinessProfile
import com.mavo.app.data.LedgerEntry
import com.mavo.app.data.Party
import com.mavo.app.data.Product
import com.mavo.app.data.Voucher
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.LocalAppWindowSizeClass
import com.mavo.app.ui.screens.mobile.MobileReportsScreen
import com.mavo.app.ui.screens.mobile.AgedBillsListView as MobileAgedBillsListView
import com.mavo.app.ui.screens.mobile.PayablesBillsListView as MobilePayablesBillsListView
import com.mavo.app.ui.screens.tablet.TabletReportsScreen
import com.mavo.app.ui.screens.tablet.AgedBillsListView as TabletAgedBillsListView
import com.mavo.app.ui.screens.tablet.PayablesBillsListView as TabletPayablesBillsListView
import com.mavo.app.ui.usesNavigationRail

/**
 * Dispatcher router for ReportsScreen.
 * Delegates to MobileReportsScreen or TabletReportsScreen based on device window size.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: AppViewModel,
    isDesktop: Boolean = false,
    modifier: Modifier = Modifier,
    navigateToLedgerBooks: () -> Unit,
    navigateToExpenses: () -> Unit = {},
    navigateToNewVoucher: (String?) -> Unit,
    onNavigateBack: () -> Unit
) {
    val windowSizeClass = LocalAppWindowSizeClass.current
    val isTablet = windowSizeClass.usesNavigationRail() || isDesktop

    if (isTablet) {
        TabletReportsScreen(
            viewModel = viewModel,
            isDesktop = isDesktop,
            modifier = modifier,
            navigateToLedgerBooks = navigateToLedgerBooks,
            navigateToExpenses = navigateToExpenses,
            navigateToNewVoucher = navigateToNewVoucher,
            onNavigateBack = onNavigateBack
        )
    } else {
        MobileReportsScreen(
            viewModel = viewModel,
            isDesktop = isDesktop,
            modifier = modifier,
            navigateToLedgerBooks = navigateToLedgerBooks,
            navigateToExpenses = navigateToExpenses,
            navigateToNewVoucher = navigateToNewVoucher,
            onNavigateBack = onNavigateBack
        )
    }
}

@Composable
fun PayablesBillsListView(
    vouchers: List<Voucher>,
    parties: List<Party>,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier,
    navigateToNewVoucher: (String?) -> Unit
) {
    val windowSizeClass = LocalAppWindowSizeClass.current
    val isTablet = windowSizeClass.usesNavigationRail()

    if (isTablet) {
        TabletPayablesBillsListView(
            vouchers = vouchers,
            parties = parties,
            viewModel = viewModel,
            modifier = modifier,
            navigateToNewVoucher = navigateToNewVoucher
        )
    } else {
        MobilePayablesBillsListView(
            vouchers = vouchers,
            parties = parties,
            viewModel = viewModel,
            modifier = modifier,
            navigateToNewVoucher = navigateToNewVoucher
        )
    }
}

@Composable
fun AgedBillsListView(
    bills: List<com.mavo.app.data.BillReceivable>,
    parties: List<Party>,
    viewModel: AppViewModel,
    modifier: Modifier = Modifier,
    navigateToNewVoucher: (String?) -> Unit
) {
    val windowSizeClass = LocalAppWindowSizeClass.current
    val isTablet = windowSizeClass.usesNavigationRail()

    if (isTablet) {
        TabletAgedBillsListView(
            bills = bills,
            parties = parties,
            viewModel = viewModel,
            modifier = modifier,
            navigateToNewVoucher = navigateToNewVoucher
        )
    } else {
        MobileAgedBillsListView(
            bills = bills,
            parties = parties,
            viewModel = viewModel,
            modifier = modifier,
            navigateToNewVoucher = navigateToNewVoucher
        )
    }
}
