package com.mavo.app.ui.theme

import android.app.Application
import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppTheme(
    val name: String,
    val backgroundPrimary: Color,
    val backgroundSecondary: Color,
    val backgroundTertiary: Color,
    val accentPrimary: Color,
    val accentLight: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val statusBarColor: Color,
    val statusBarDarkIcons: Boolean,
    val isDark: Boolean = false
) {
    companion object {
        // Harmonious 60:30:10 Cafe themes
        val ZERO = AppTheme(
            name = ThemeNames.ZERO,
            backgroundPrimary = Color(0xFFFBF8F4), // 60% warm latte cream
            backgroundSecondary = Color(0xFFFFFDFB), // Surface card
            backgroundTertiary = Color(0xFFF2EDE5),  // Sub-folder container
            accentPrimary = Color(0xFFC86D44),       // 10% Warm caramel terracotta accent
            accentLight = Color(0xFFF6E7DD),
            textPrimary = Color(0xFF2E1F16),         // 30% Espresso structure
            textSecondary = Color(0xFF6E5A4D),
            textTertiary = Color(0xFF9E8B7E),
            statusBarColor = Color(0xFFFBF8F4),
            statusBarDarkIcons = true
        )

        val SAFFRON = AppTheme(
            name = ThemeNames.SAFFRON,
            backgroundPrimary = Color(0xFFFBF8F4),
            backgroundSecondary = Color(0xFFFFFDFB),
            backgroundTertiary = Color(0xFFF4ECE2),
            accentPrimary = Color(0xFFD6783C),
            accentLight = Color(0xFFFBEADB),
            textPrimary = Color(0xFF2E1F16),
            textSecondary = Color(0xFF6E5A4D),
            textTertiary = Color(0xFF9E8B7E),
            statusBarColor = Color(0xFFFBF8F4),
            statusBarDarkIcons = true
        )

        val SLATE = AppTheme(
            name = ThemeNames.SLATE,
            backgroundPrimary = Color(0xFFFBF8F4),
            backgroundSecondary = Color(0xFFFFFDFB),
            backgroundTertiary = Color(0xFFEDE9E3),
            accentPrimary = Color(0xFF5D4432),
            accentLight = Color(0xFFE8E0D7),
            textPrimary = Color(0xFF2E1F16),
            textSecondary = Color(0xFF6E5A4D),
            textTertiary = Color(0xFF9E8B7E),
            statusBarColor = Color(0xFFFBF8F4),
            statusBarDarkIcons = true
        )

        val LEDGER = AppTheme(
            name = ThemeNames.LEDGER,
            backgroundPrimary = Color(0xFFFBF8F4),
            backgroundSecondary = Color(0xFFFFFDFB),
            backgroundTertiary = Color(0xFFE9F1E8),
            accentPrimary = Color(0xFF4E7756),
            accentLight = Color(0xFFDDEADE),
            textPrimary = Color(0xFF2E1F16),
            textSecondary = Color(0xFF6E5A4D),
            textTertiary = Color(0xFF9E8B7E),
            statusBarColor = Color(0xFFFBF8F4),
            statusBarDarkIcons = true
        )

        val NIGHT = ZERO

        fun fromName(themeName: String): AppTheme = when (themeName) {
            ThemeNames.SAFFRON -> SAFFRON
            ThemeNames.SLATE -> SLATE
            ThemeNames.LEDGER -> LEDGER
            else -> ZERO
        }
    }
}

val LocalAppTheme = staticCompositionLocalOf { AppTheme.ZERO }

object ThemeRuntime {
    val currentTheme = mutableStateOf(AppTheme.ZERO)
}

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("app_theme_prefs", Context.MODE_PRIVATE)

    private val _currentTheme = MutableStateFlow(loadTheme())
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    private fun loadTheme(): AppTheme {
        val themeName = prefs.getString("selected_theme", ThemeNames.ZERO) ?: ThemeNames.ZERO
        val theme = AppTheme.fromName(themeName)
        ThemeRuntime.currentTheme.value = theme
        return theme
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            prefs.edit().putString("selected_theme", theme.name).apply()
            ThemeRuntime.currentTheme.value = theme
            _currentTheme.value = theme
        }
    }

    fun setTheme(themeName: String) {
        setTheme(AppTheme.fromName(themeName))
    }
}

@Composable
fun MavoTheme(
    appTheme: AppTheme = LocalAppTheme.current,
    content: @Composable () -> Unit
) {
    val colorScheme = lightColorScheme(
        primary = appTheme.accentPrimary,
        onPrimary = Color.White,
        primaryContainer = appTheme.accentLight,
        onPrimaryContainer = appTheme.textPrimary,
        secondary = appTheme.textSecondary,
        onSecondary = Color.White,
        background = appTheme.backgroundPrimary,
        onBackground = appTheme.textPrimary,
        surface = appTheme.backgroundSecondary,
        onSurface = appTheme.textPrimary,
        surfaceVariant = appTheme.backgroundTertiary,
        onSurfaceVariant = appTheme.textSecondary,
        outline = Surface.border
    )

    CompositionLocalProvider(
        LocalAppTheme provides appTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object MavoTextFieldDefaults {
    @Composable
    fun colors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = AppColors.textPrimary,
        unfocusedTextColor = AppColors.textPrimary,
        focusedContainerColor = Surface.input,
        unfocusedContainerColor = Surface.input,
        disabledContainerColor = Surface.input,
        focusedBorderColor = Surface.inputBorderFocus,
        unfocusedBorderColor = Surface.inputBorder,
        focusedLabelColor = AppColors.primary,
        unfocusedLabelColor = AppColors.textSecondary,
        focusedPlaceholderColor = AppColors.textTertiary,
        unfocusedPlaceholderColor = AppColors.textTertiary,
        cursorColor = AppColors.primary
    )
}

@Composable
fun mavoInputColors(): TextFieldColors = MavoTextFieldDefaults.colors()
