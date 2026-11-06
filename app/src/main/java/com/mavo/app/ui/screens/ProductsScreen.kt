package com.mavo.app.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mavo.app.data.Product
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.LocalAppWindowSizeClass
import com.mavo.app.ui.screens.common.ProductSheetMode
import com.mavo.app.ui.screens.mobile.MobileProductsScreen
import com.mavo.app.ui.screens.mobile.ProductEditorScreen as MobileProductEditorScreen
import com.mavo.app.ui.screens.tablet.TabletProductsScreen
import com.mavo.app.ui.screens.tablet.ProductEditorScreen as TabletProductEditorScreen
import com.mavo.app.ui.usesNavigationRail

typealias ProductSheetMode = com.mavo.app.ui.screens.common.ProductSheetMode

fun Product.isLowStockAlertTriggered(): Boolean =
    enableStockAlert && currentStock <= lowStockThreshold

/**
 * Dispatcher router for ProductsScreen.
 * Delegates to MobileProductsScreen or TabletProductsScreen based on device window size.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToLowStock: () -> Unit
) {
    val windowSizeClass = LocalAppWindowSizeClass.current
    val isTablet = windowSizeClass.usesNavigationRail()

    if (isTablet) {
        TabletProductsScreen(
            viewModel = viewModel,
            onNavigateBack = onNavigateBack,
            modifier = modifier,
            onNavigateToLowStock = onNavigateToLowStock
        )
    } else {
        MobileProductsScreen(
            viewModel = viewModel,
            onNavigateBack = onNavigateBack,
            modifier = modifier,
            onNavigateToLowStock = onNavigateToLowStock
        )
    }
}

@Composable
fun ProductEditorScreen(
    viewModel: AppViewModel,
    mode: ProductSheetMode,
    existingProduct: Product?,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    val windowSizeClass = LocalAppWindowSizeClass.current
    val isTablet = windowSizeClass.usesNavigationRail()

    if (isTablet) {
        TabletProductEditorScreen(
            viewModel = viewModel,
            mode = mode,
            existingProduct = existingProduct,
            modifier = modifier,
            onDismiss = onDismiss
        )
    } else {
        MobileProductEditorScreen(
            viewModel = viewModel,
            mode = mode,
            existingProduct = existingProduct,
            modifier = modifier,
            onDismiss = onDismiss
        )
    }
}
