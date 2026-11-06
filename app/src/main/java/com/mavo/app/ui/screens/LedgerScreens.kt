package com.mavo.app.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.LocalAppWindowSizeClass
import com.mavo.app.ui.screens.mobile.MobileExpensesScreen
import com.mavo.app.ui.screens.tablet.TabletExpensesScreen
import com.mavo.app.ui.usesNavigationRail

internal val incomeCategories = listOf(
    "Sales Commission", "Interest Received", "Rental Income", "Freelance Services",
    "Consulting Fees", "Investment Returns", "Grants", "Donations",
    "Refunds Received", "Other Income"
)

internal val expenseCategories = listOf(
    "Rent", "Electricity", "Water", "Telephone", "Internet",
    "Staff Salary", "Transport", "Packaging", "Maintenance",
    "Office Supplies", "Advertising", "Insurance", "Bank Charges",
    "Miscellaneous", "Other"
)

/**
 * Dispatcher router for ExpensesScreen (LedgerScreens).
 * Delegates to MobileExpensesScreen or TabletExpensesScreen based on device window size.
 */
@Composable
fun ExpensesScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = LocalAppWindowSizeClass.current
    val isTablet = windowSizeClass.usesNavigationRail()

    if (isTablet) {
        TabletExpensesScreen(
            viewModel = viewModel,
            onNavigateBack = onNavigateBack,
            modifier = modifier
        )
    } else {
        MobileExpensesScreen(
            viewModel = viewModel,
            onNavigateBack = onNavigateBack,
            modifier = modifier
        )
    }
}
