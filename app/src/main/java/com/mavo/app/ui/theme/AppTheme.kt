package com.mavo.app.ui.theme

import android.app.Application
import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
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
        // Mavo Loop 60:30:10 Single Canonical Theme
        // 60% Peach Paper canvas (#F6E0CF)
        // 30% Aubergine Ink structure & text (#3A263A)
        // 10% Mango Punch accent (#F2A83B)
        val MAVO = AppTheme(
            name = ThemeNames.MAVO,
            backgroundPrimary = Color(0xFFF6E0CF), // 60% Peach Paper
            backgroundSecondary = Color(0xFFFAF0E6), // Light Peach card surface
            backgroundTertiary = Color(0xFFEED6C2),  // Grouped surface
            accentPrimary = Color(0xFFF2A83B),       // 10% Mango Punch
            accentLight = Color(0xFFFADAA8),
            textPrimary = Color(0xFF3A263A),         // 30% Aubergine Ink
            textSecondary = Color(0xFF5E455E),
            textTertiary = Color(0xFF8A718A),
            statusBarColor = Color(0xFFF6E0CF),
            statusBarDarkIcons = true
        )

        val ZERO = MAVO
        val SAFFRON = MAVO
        val SLATE = MAVO
        val LEDGER = MAVO
        val NIGHT = MAVO

        fun fromName(themeName: String): AppTheme = MAVO
    }
}

val LocalAppTheme = staticCompositionLocalOf { AppTheme.MAVO }
val LocalMavoSpacing = staticCompositionLocalOf { SpacingTokens }
val LocalMavoShapes = staticCompositionLocalOf { ShapeTokens }
val LocalMavoIcons = staticCompositionLocalOf { IconTokens }
val LocalMavoDimensions = staticCompositionLocalOf { DimensionTokens }
val LocalMavoTextSizes = staticCompositionLocalOf { TextSizeTokens }

object ThemeRuntime {
    val currentTheme = mutableStateOf(AppTheme.MAVO)
}

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("app_theme_prefs", Context.MODE_PRIVATE)

    private val _currentTheme = MutableStateFlow(loadTheme())
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    private fun loadTheme(): AppTheme {
        ThemeRuntime.currentTheme.value = AppTheme.MAVO
        return AppTheme.MAVO
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            prefs.edit().putString("selected_theme", AppTheme.MAVO.name).apply()
            ThemeRuntime.currentTheme.value = AppTheme.MAVO
            _currentTheme.value = AppTheme.MAVO
        }
    }

    fun setTheme(themeName: String) {
        setTheme(AppTheme.MAVO)
    }
}

@Composable
fun MavoTheme(
    appTheme: AppTheme = LocalAppTheme.current,
    content: @Composable () -> Unit
) {
    val colorScheme = lightColorScheme(
        primary = appTheme.accentPrimary,
        onPrimary = appTheme.textPrimary, // Never white on Mango! Aubergine on Mango 6.9:1
        primaryContainer = appTheme.accentLight,
        onPrimaryContainer = appTheme.textPrimary,
        secondary = appTheme.textSecondary,
        onSecondary = Color(0xFFF6E0CF),
        background = appTheme.backgroundPrimary,
        onBackground = appTheme.textPrimary,
        surface = appTheme.backgroundSecondary,
        onSurface = appTheme.textPrimary,
        surfaceVariant = appTheme.backgroundTertiary,
        onSurfaceVariant = appTheme.textSecondary,
        outline = Surface.border
    )

    CompositionLocalProvider(
        LocalAppTheme provides appTheme,
        LocalMavoSpacing provides SpacingTokens,
        LocalMavoShapes provides ShapeTokens,
        LocalMavoIcons provides IconTokens,
        LocalMavoDimensions provides DimensionTokens,
        LocalMavoTextSizes provides TextSizeTokens
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
