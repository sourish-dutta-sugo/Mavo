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
        // Graph-paper canvas stays fixed while theme choices change the accent.
        val ZERO = AppTheme(
            name = ThemeNames.ZERO,
            backgroundPrimary = Color.Transparent,
            backgroundSecondary = Color(0xFFFFFCF7),
            backgroundTertiary = Color(0xFFF0ECE4),
            accentPrimary = Color(0xFF006B5F),
            accentLight = Color(0xFFDCEFEA),
            textPrimary = Color(0xFF211F1A),
            textSecondary = Color(0xFF4D4A43),
            textTertiary = Color(0xFF77736B),
            statusBarColor = Color(0xFFF7F5EF),
            statusBarDarkIcons = true
        )

        val SAFFRON = AppTheme(
            name = ThemeNames.SAFFRON,
            backgroundPrimary = Color.Transparent,
            backgroundSecondary = Color(0xFFFFFCF7),
            backgroundTertiary = Color(0xFFF0ECE4),
            accentPrimary = Color(0xFFA85F00),
            accentLight = Color(0xFFFFF0DA),
            textPrimary = Color(0xFF211F1A),
            textSecondary = Color(0xFF4D4A43),
            textTertiary = Color(0xFF77736B),
            statusBarColor = Color(0xFFF7F5EF),
            statusBarDarkIcons = true
        )

        val SLATE = AppTheme(
            name = ThemeNames.SLATE,
            backgroundPrimary = Color.Transparent,
            backgroundSecondary = Color(0xFFFFFCF7),
            backgroundTertiary = Color(0xFFF0ECE4),
            accentPrimary = Color(0xFF4D4A43),
            accentLight = Color(0xFFEDEAE2),
            textPrimary = Color(0xFF211F1A),
            textSecondary = Color(0xFF4D4A43),
            textTertiary = Color(0xFF77736B),
            statusBarColor = Color(0xFFF7F5EF),
            statusBarDarkIcons = true
        )

        val LEDGER = AppTheme(
            name = ThemeNames.LEDGER,
            backgroundPrimary = Color.Transparent,
            backgroundSecondary = Color(0xFFFFFCF7),
            backgroundTertiary = Color(0xFFF0ECE4),
            accentPrimary = Color(0xFF176B4A),
            accentLight = Color(0xFFDCEFEA),
            textPrimary = Color(0xFF211F1A),
            textSecondary = Color(0xFF4D4A43),
            textTertiary = Color(0xFF77736B),
            statusBarColor = Color(0xFFF7F5EF),
            statusBarDarkIcons = true
        )

        // Ink (purple) retired: every accent now sits on the paper palette.
        // Legacy saved names ("INK", "PURPLE", "DARK") fall through to ZERO.

        val NIGHT = ZERO

        fun fromName(name: String?): AppTheme = when (name?.uppercase()) {
            ThemeNames.SLATE, "BLUE" -> SLATE
            ThemeNames.LEDGER, "GREEN" -> LEDGER
            ThemeNames.NIGHT -> ZERO
            ThemeNames.SAFFRON, "BEACH", "TEAL" -> SAFFRON
            ThemeNames.ZERO -> ZERO
            else -> ZERO
        }
    }
}

private object ThemeStorage {
    private const val PREFS_NAME = "mavo_pref"
    private const val KEY_SELECTED_THEME = "selected_theme"

    fun load(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_SELECTED_THEME, AppTheme.ZERO.name)
            ?: AppTheme.ZERO.name
    }

    fun save(context: Context, name: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SELECTED_THEME, name)
            .apply()
    }
}

object ThemeRuntime {
    val currentTheme = mutableStateOf(AppTheme.ZERO)

    fun apply(theme: AppTheme) {
        currentTheme.value = theme
    }
}

val LocalAppTheme = staticCompositionLocalOf {
    AppTheme.ZERO
}

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentTheme = MutableStateFlow(AppTheme.ZERO)
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    init {
        val theme = AppTheme.fromName(ThemeStorage.load(application))
        _currentTheme.value = theme
        ThemeRuntime.apply(theme)
    }

    fun setTheme(name: String) {
        val theme = AppTheme.fromName(name)
        viewModelScope.launch {
            ThemeStorage.save(getApplication(), theme.name)
            _currentTheme.value = theme
            ThemeRuntime.apply(theme)
        }
    }
}

@Composable
fun MavoTheme(
    appTheme: AppTheme = LocalAppTheme.current,
    content: @Composable () -> Unit
) {
    val colorScheme = lightColorScheme(
        primary = appTheme.accentPrimary,
        onPrimary = TextColors.onPrimary,
        primaryContainer = appTheme.accentLight,
        onPrimaryContainer = appTheme.textPrimary,
        secondary = appTheme.textSecondary,
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = appTheme.backgroundTertiary,
        onSecondaryContainer = appTheme.textPrimary,
        tertiary = Color(0xFF8A4A00),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Semantic.warningBg,
        onTertiaryContainer = Color(0xFF4A2800),
        error = Semantic.error,
        onError = Color(0xFFFFFFFF),
        errorContainer = Semantic.errorBg,
        onErrorContainer = appTheme.textPrimary,
        background = appTheme.backgroundPrimary,
        surface = appTheme.backgroundSecondary,
        outline = Color(0xFF77736B),
        outlineVariant = Color(0xFFD2CEC5),
        onBackground = appTheme.textPrimary,
        onSurface = appTheme.textPrimary,
        surfaceVariant = appTheme.backgroundTertiary,
        onSurfaceVariant = appTheme.textSecondary
    )

    val currentDensity = LocalDensity.current
    val fixedDensity = remember(currentDensity.density) {
        Density(
            density = currentDensity.density,
            fontScale = 1.0f
        )
    }

    CompositionLocalProvider(
        LocalDensity provides fixedDensity
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun themedInputColors(): TextFieldColors {
    val theme = LocalAppTheme.current
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = theme.textPrimary,
        unfocusedTextColor = theme.textPrimary,
        disabledTextColor = TextColors.disabled,
        focusedPlaceholderColor = theme.textTertiary,
        unfocusedPlaceholderColor = theme.textTertiary,
        focusedBorderColor = theme.accentPrimary,
        unfocusedBorderColor = Surface.inputBorder,
        focusedLabelColor = theme.accentPrimary,
        unfocusedLabelColor = theme.textSecondary,
        cursorColor = theme.accentPrimary,
        focusedContainerColor = theme.backgroundSecondary,
        unfocusedContainerColor = theme.backgroundSecondary,
        errorTextColor = theme.textPrimary,
        errorBorderColor = Semantic.error,
        errorContainerColor = theme.backgroundSecondary,
        errorLabelColor = Semantic.error,
        errorPlaceholderColor = theme.textTertiary,
        disabledContainerColor = theme.backgroundSecondary,
        disabledBorderColor = Surface.inputBorder
    )
}

@Composable
fun mavoInputColors(): TextFieldColors = themedInputColors()
