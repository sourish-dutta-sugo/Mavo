package com.zerobook.app.feature.billing

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ResponsivePaneLayoutTest {
    @Test
    fun compactAndMediumUseSinglePane() {
        assertFalse(usesSplitPane(WindowWidthSizeClass.Compact))
        assertFalse(usesSplitPane(WindowWidthSizeClass.Medium))
    }

    @Test
    fun expandedUsesSplitPane() {
        assertTrue(usesSplitPane(WindowWidthSizeClass.Expanded))
    }
}
