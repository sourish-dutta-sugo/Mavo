package com.mavo.app.ui.theme

import android.app.Application
import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.foundation.isSystemInDarkTheme
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
        val MAVO = AppTheme(
            name = ThemeNames.MAVO,
            backgroundPrimary = Color(0xFFF6E0CF),
            backgroundSecondary = Color(0xFFFAF3EC),
            backgroundTertiary = Color(0xFFEDE0D4),
            accentPrimary = Color(0xFFF2A83B),
            accentLight = Color(0xFFFBEAD8),
            textPrimary = Color(0xFF3E2B1E),
            textSecondary = Color(0xFF6B5344),
            textTertiary = Color(0xFF8C7360),
            statusBarColor = Color(0xFFF6E0CF),
            statusBarDarkIcons = true
        )

        val DARK = AppTheme(
            name = ThemeNames.MAVO,
            backgroundPrimary = Color(0xFF1C1B1F),
            backgroundSecondary = Color(0xFF2B2624),
            backgroundTertiary = Color(0xFF3A322E),
            accentPrimary = Color(0xFFF2A83B),
            accentLight = Color(0xFF3A263A),
            textPrimary = Color(0xFFF6E0CF),
            textSecondary = Color(0xFFEDE0D4),
            textTertiary = Color(0xFFAD9886),
            statusBarColor = Color(0xFF1C1B1F),
            statusBarDarkIcons = false,
            isDark = true
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
    darkTheme: Boolean = isSystemInDarkTheme(),
    increasedContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val resolvedTheme = if (darkTheme) AppTheme.DARK else appTheme
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFFF2A83B),
            onPrimary = Color(0xFF3A263A),
            primaryContainer = Color(0xFF3A263A),
            onPrimaryContainer = Color(0xFFF6E0CF),
            secondary = Color(0xFFEDE0D4),
            onSecondary = Color(0xFF1C1B1F),
            background = Color(0xFF1C1B1F),
            onBackground = Color(0xFFF6E0CF),
            surface = Color(0xFF2B2624),
            onSurface = Color(0xFFF6E0CF),
            surfaceVariant = Color(0xFF3A322E),
            onSurfaceVariant = Color(0xFFEDE0D4),
            outline = Color(0xFF8C7360)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFFF2A83B),
            onPrimary = Color(0xFF3A263A),
            primaryContainer = Color(0xFF3A263A),
            onPrimaryContainer = Color(0xFFF6E0CF),
            secondary = Color(0xFF6B5344),
            onSecondary = Color(0xFFF6E0CF),
            background = Color(0xFFF6E0CF),
            onBackground = Color(0xFF3E2B1E),
            surface = Color(0xFFFAF3EC),
            onSurface = Color(0xFF3E2B1E),
            surfaceVariant = Color(0xFFEDE0D4),
            onSurfaceVariant = Color(0xFF6B5344),
            outline = if (increasedContrast) Color(0xFF3E2B1E) else Color(0xFF3A263A).copy(alpha = 0.25f)
        )
    }

    androidx.compose.runtime.SideEffect {
        ThemeRuntime.currentTheme.value = resolvedTheme
    }

    CompositionLocalProvider(
        LocalAppTheme provides resolvedTheme
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
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = Color.Transparent,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        cursorColor = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun mavoInputColors(): TextFieldColors = MavoTextFieldDefaults.colors()
