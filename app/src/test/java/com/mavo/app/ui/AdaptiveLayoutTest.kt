package com.mavo.app.ui

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveLayoutTest {
    @Test
    fun compactUsesBottomNavigation() {
        assertFalse(WindowWidthSizeClass.Compact.usesNavigationRail())
    }

    @Test
    fun mediumAndExpandedUseNavigationRail() {
        assertTrue(WindowWidthSizeClass.Medium.usesNavigationRail())
        assertTrue(WindowWidthSizeClass.Expanded.usesNavigationRail())
    }

    @Test
    fun onlyExpandedWindowsUseTwoPaneContent() {
        assertFalse(WindowWidthSizeClass.Compact.usesTwoPaneLayout())
        assertFalse(WindowWidthSizeClass.Medium.usesTwoPaneLayout())
        assertTrue(WindowWidthSizeClass.Expanded.usesTwoPaneLayout())
    }

    @Test
    fun dashboardContentGetsAdaptiveMaximumWidth() {
        assertTrue(WindowWidthSizeClass.Compact.dashboardMaxWidthDp() == null)
        assertTrue(WindowWidthSizeClass.Medium.dashboardMaxWidthDp() == 960)
        assertTrue(WindowWidthSizeClass.Expanded.dashboardMaxWidthDp() == 1280)
    }
}
