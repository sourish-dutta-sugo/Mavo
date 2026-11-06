package com.mavo.app.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mavo.app.data.Party
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.LocalAppWindowSizeClass
import com.mavo.app.ui.screens.mobile.MobilePartiesScreen
import com.mavo.app.ui.screens.mobile.PartyDetailScreen as MobilePartyDetailScreen
import com.mavo.app.ui.screens.mobile.AddPartyForm as MobileAddPartyForm
import com.mavo.app.ui.screens.mobile.EditPartyForm as MobileEditPartyForm
import com.mavo.app.ui.screens.tablet.TabletPartiesScreen
import com.mavo.app.ui.screens.tablet.PartyDetailScreen as TabletPartyDetailScreen
import com.mavo.app.ui.screens.tablet.AddPartyForm as TabletAddPartyForm
import com.mavo.app.ui.screens.tablet.EditPartyForm as TabletEditPartyForm
import com.mavo.app.ui.usesNavigationRail

/**
 * Dispatcher router for PartiesScreen.
 * Delegates to MobilePartiesScreen or TabletPartiesScreen based on device window size.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartiesScreen(
    viewModel: AppViewModel,
    isDesktop: Boolean = false,
    modifier: Modifier = Modifier,
    onPartySelected: (String) -> Unit
) {
    val windowSizeClass = LocalAppWindowSizeClass.current
    val isTablet = windowSizeClass.usesNavigationRail() || isDesktop

    if (isTablet) {
        TabletPartiesScreen(
            viewModel = viewModel,
            isDesktop = isDesktop,
            modifier = modifier,
            onPartySelected = onPartySelected
        )
    } else {
        MobilePartiesScreen(
            viewModel = viewModel,
            isDesktop = isDesktop,
            modifier = modifier,
            onPartySelected = onPartySelected
        )
    }
}

@Composable
fun PartyDetailScreen(
    viewModel: AppViewModel,
    partyId: String,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    val windowSizeClass = LocalAppWindowSizeClass.current
    val isTablet = windowSizeClass.usesNavigationRail()

    if (isTablet) {
        TabletPartyDetailScreen(
            viewModel = viewModel,
            partyId = partyId,
            modifier = modifier,
            onNavigateBack = onNavigateBack
        )
    } else {
        MobilePartyDetailScreen(
            viewModel = viewModel,
            partyId = partyId,
            modifier = modifier,
            onNavigateBack = onNavigateBack
        )
    }
}

@Composable
fun AddPartyForm(
    viewModel: AppViewModel,
    onDismiss: () -> Unit
) {
    val windowSizeClass = LocalAppWindowSizeClass.current
    val isTablet = windowSizeClass.usesNavigationRail()

    if (isTablet) {
        TabletAddPartyForm(
            viewModel = viewModel,
            onDismiss = onDismiss
        )
    } else {
        MobileAddPartyForm(
            viewModel = viewModel,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun EditPartyForm(
    viewModel: AppViewModel,
    party: Party,
    onDismiss: () -> Unit
) {
    val windowSizeClass = LocalAppWindowSizeClass.current
    val isTablet = windowSizeClass.usesNavigationRail()

    if (isTablet) {
        TabletEditPartyForm(
            viewModel = viewModel,
            party = party,
            onDismiss = onDismiss
        )
    } else {
        MobileEditPartyForm(
            viewModel = viewModel,
            party = party,
            onDismiss = onDismiss
        )
    }
}
