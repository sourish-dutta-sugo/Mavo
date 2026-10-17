package com.mavo.app.ui

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

@Immutable
data class AppNavigationDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val LocalAppWindowSizeClass = staticCompositionLocalOf {
    WindowWidthSizeClass.Compact
}

fun WindowWidthSizeClass.usesNavigationRail(): Boolean =
    this != WindowWidthSizeClass.Compact

fun WindowWidthSizeClass.usesTwoPaneLayout(): Boolean =
    this == WindowWidthSizeClass.Expanded

fun WindowWidthSizeClass.dashboardMaxWidthDp(): Int? = when (this) {
    WindowWidthSizeClass.Compact -> null
    WindowWidthSizeClass.Medium -> 960
    else -> 1280
}
