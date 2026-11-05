package com.mavo.app.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AppThemeTest {
    @Test
    fun savedNightThemeFallsBackToLightZeroTheme() {
        val theme = AppTheme.fromName(ThemeNames.NIGHT)

        assertEquals(AppTheme.ZERO, theme)
        assertFalse(theme.isDark)
    }
}
