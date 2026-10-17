package com.mavo.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mavo.app.ui.AppNavigationDestination

@Composable
fun AdaptiveNavigationBar(
    destinations: List<AppNavigationDestination>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onNewSale: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        destinations.forEach { destination ->
            val selected = currentRoute == destination.route
            NavigationBarItem(
                selected = selected,
                onClick = { if (!selected) onNavigate(destination.route) },
                icon = { Icon(destination.icon, contentDescription = null) },
                label = { Text(destination.label) }
            )
        }
        NavigationBarItem(
            selected = false,
            onClick = onNewSale,
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            label = { Text("New Sale") }
        )
    }
}

@Composable
fun AdaptiveNavigationRail(
    destinations: List<AppNavigationDestination>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onNewSale: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier,
        header = {
            FloatingActionButton(onClick = onNewSale) {
                Icon(Icons.Default.Add, contentDescription = "New Sale")
            }
        }
    ) {
        destinations.forEach { destination ->
            val selected = currentRoute == destination.route
            NavigationRailItem(
                selected = selected,
                onClick = { if (!selected) onNavigate(destination.route) },
                icon = { Icon(destination.icon, contentDescription = null) },
                label = { Text(destination.label) }
            )
        }
    }
}
