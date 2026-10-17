package com.mavo.app.ui.theme

import android.app.Application
import android.content.Context
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
        // Shared warm surfaces keep theme choices focused on brand accent.
        val ZERO = AppTheme(
            name = ThemeNames.ZERO,
            backgroundPrimary = Color(0xFFF7F5EF),
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
            backgroundPrimary = Color(0xFFF7F5EF),
            backgroundSecondary = Color(0xFFFFFCF7),
            backgroundTertiary = Color(0xFFF0ECE4),
            accentPrimary = Color(0xFF1A5C4B),
            accentLight = Color(0xFFDCEFEA),
            textPrimary = Color(0xFF211F1A),
            textSecondary = Color(0xFF4D4A43),
            textTertiary = Color(0xFF77736B),
            statusBarColor = Color(0xFFF7F5EF),
            statusBarDarkIcons = true
        )

        val SLATE = AppTheme(
            name = ThemeNames.SLATE,
            backgroundPrimary = Color(0xFFF7F5EF),
            backgroundSecondary = Color(0xFFFFFCF7),
            backgroundTertiary = Color(0xFFF0ECE4),
            accentPrimary = Color(0xFF22755F),
            accentLight = Color(0xFFDCEFEA),
            textPrimary = Color(0xFF211F1A),
            textSecondary = Color(0xFF4D4A43),
            textTertiary = Color(0xFF77736B),
            statusBarColor = Color(0xFFF7F5EF),
            statusBarDarkIcons = true
        )

        val LEDGER = AppTheme(
            name = ThemeNames.LEDGER,
            backgroundPrimary = Color(0xFFF7F5EF),
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

        val INK = AppTheme(
            name = ThemeNames.INK,
            backgroundPrimary = Color(0xFFF7F5EF),
            backgroundSecondary = Color(0xFFFFFCF7),
            backgroundTertiary = Color(0xFFF0ECE4),
            accentPrimary = Color(0xFF7C3AED),
            accentLight = Color(0xFFEDE6FF),
            textPrimary = Color(0xFF211F1A),
            textSecondary = Color(0xFF4D4A43),
            textTertiary = Color(0xFF77736B),
            statusBarColor = Color(0xFFF7F5EF),
            statusBarDarkIcons = true
        )

        val NIGHT = AppTheme(
            name = ThemeNames.NIGHT,
            backgroundPrimary = Color(0xFF101615),
            backgroundSecondary = Color(0xFF18211F),
            backgroundTertiary = Color(0xFF22302C),
            accentPrimary = Color(0xFF72D2B2),
            accentLight = Color(0xFF213C35),
            textPrimary = Color(0xFFF1F5F3),
            textSecondary = Color(0xFFB8C8C2),
            textTertiary = Color(0xFF81938C),
            statusBarColor = Color(0xFF101615),
            statusBarDarkIcons = false,
            isDark = true
        )

        fun fromName(name: String?): AppTheme = when (name?.uppercase()) {
            ThemeNames.SLATE, "BLUE" -> SLATE
            ThemeNames.LEDGER, "GREEN" -> LEDGER
            ThemeNames.INK, "PURPLE", "DARK" -> INK
            ThemeNames.NIGHT -> NIGHT
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
    val colorScheme = (if (appTheme.isDark) darkColorScheme(
        primary = appTheme.accentPrimary,
        onPrimary = Color(0xFF082018),
        primaryContainer = appTheme.accentLight,
        onPrimaryContainer = appTheme.textPrimary,
        secondary = appTheme.textSecondary,
        onSecondary = appTheme.backgroundPrimary,
        secondaryContainer = appTheme.backgroundTertiary,
        onSecondaryContainer = appTheme.textPrimary,
        tertiary = Color(0xFFFFB95C),
        onTertiary = Color(0xFF452B00),
        tertiaryContainer = Color(0xFF49371D),
        onTertiaryContainer = Color(0xFFFFDEA6),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        background = appTheme.backgroundPrimary,
        surface = appTheme.backgroundSecondary,
        outline = appTheme.textTertiary.copy(alpha = 0.45f),
        outlineVariant = appTheme.textTertiary.copy(alpha = 0.24f),
        onBackground = appTheme.textPrimary,
        onSurface = appTheme.textPrimary,
        surfaceVariant = appTheme.backgroundTertiary,
        onSurfaceVariant = appTheme.textSecondary
    ) else lightColorScheme(
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
        error = Color(0xFFB3261E),
        onError = Color(0xFFFFFFFF),
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        background = appTheme.backgroundPrimary,
        surface = appTheme.backgroundSecondary,
        outline = Color(0xFF77736B),
        outlineVariant = Color(0xFFD2CEC5),
        onBackground = appTheme.textPrimary,
        onSurface = appTheme.textPrimary,
        surfaceVariant = appTheme.backgroundTertiary,
        onSurfaceVariant = appTheme.textSecondary
    ))

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
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
