package com.mavo.app.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.DashboardViewModel
import com.mavo.app.ui.LocalAppWindowSizeClass
import com.mavo.app.ui.screens.mobile.MobileDashboardScreen
import com.mavo.app.ui.screens.tablet.TabletDashboardScreen
import com.mavo.app.ui.usesNavigationRail

@Immutable
data class DashboardBalanceSnapshot(
    val cashBalance: Double = 0.0,
    val bankBalance: Double = 0.0,
    val outstandingReceivable: Double = 0.0,
    val outstandingPayable: Double = 0.0
)

@Immutable
data class KpiDetails(
    val title: String,
    val amount: String,
    val highlight: Color
)

enum class ChartType { LINE, BAR, PIE }
enum class AnalyticsFilter(val label: String) {
    TODAY("Today"), THIS_WEEK("This Week"), THIS_MONTH("This Month"),
    THIS_QUARTER("This Quarter"), THIS_YEAR("This Year"),
}

/**
 * Dispatcher router for DashboardScreen.
 * Delegates cleanly to MobileDashboardScreen or TabletDashboardScreen based on device window size.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: AppViewModel,
    dashboardViewModel: DashboardViewModel,
    isDesktop: Boolean = false,
    modifier: Modifier = Modifier,
    onQuickAction: (String) -> Unit
) {
    val windowWidthSizeClass = LocalAppWindowSizeClass.current
    val isTablet = windowWidthSizeClass.usesNavigationRail() || isDesktop

    if (isTablet) {
        TabletDashboardScreen(
            viewModel = viewModel,
            dashboardViewModel = dashboardViewModel,
            modifier = modifier,
            onQuickAction = onQuickAction
        )
    } else {
        MobileDashboardScreen(
            viewModel = viewModel,
            dashboardViewModel = dashboardViewModel,
            modifier = modifier,
            onQuickAction = onQuickAction
        )
    }
}
