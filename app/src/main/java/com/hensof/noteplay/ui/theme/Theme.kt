package com.hensof.noteplay.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = YellowAccent,
    onPrimary = Color(0xFF3E2723),
    primaryContainer = Color(0xFFFFE699),
    onPrimaryContainer = Color(0xFF3E2723),
    
    secondary = GreenAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF80E6D0),
    onSecondaryContainer = Color(0xFF00352C),
    
    tertiary = RedAccent,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFB3B3),
    onTertiaryContainer = Color(0xFF410000),
    
    background = LightBackground,
    onBackground = LightOnBackground,
    
    surface = LightSurface,
    onSurface = LightOnSurface,
    
    surfaceVariant = Color(0xFFFFF3D1),
    onSurfaceVariant = Color(0xFF4A4A4A),
    
    error = RedAccent,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    
    outline = Color(0xFFB0B0B0),
    outlineVariant = Color(0xFFE0E0E0)
)

private val DarkColorScheme = darkColorScheme(
    primary = YellowAccentDark,
    onPrimary = Color(0xFF1A1A1A),
    primaryContainer = Color(0xFFB89D00),
    onPrimaryContainer = Color(0xFFFFE699),
    
    secondary = GreenAccentDark,
    onSecondary = Color(0xFF003830),
    secondaryContainer = Color(0xFF005047),
    onSecondaryContainer = Color(0xFF80E6D0),
    
    tertiary = RedAccentDark,
    onTertiary = Color(0xFF690000),
    tertiaryContainer = Color(0xFF930000),
    onTertiaryContainer = Color(0xFFFFB3B3),
    
    background = DarkBackground,
    onBackground = DarkOnBackground,
    
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    
    surfaceVariant = Color(0xFF4A4A4C),
    onSurfaceVariant = Color(0xFFE0E0E0),
    
    error = RedAccentDark,
    onError = Color(0xFF690000),
    errorContainer = Color(0xFF930000),
    onErrorContainer = Color(0xFFFFDAD6),
    
    outline = Color(0xFF8A8A8A),
    outlineVariant = Color(0xFF4A4A4C)
)

@Composable
fun HenNotesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}