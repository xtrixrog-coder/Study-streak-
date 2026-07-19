package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val PrimaryBlue = Color(0xFFA8C8FF)
val PrimaryPurple = Color(0xFFD0BCFF)
val BackgroundDark = Color(0xFF1A1C1E)
val SurfaceDark = Color(0xFF2D2F33)
val TextDark = Color(0xFFE2E2E6)
val TextSecondary = Color(0xFFC4C6D0)
val HeroGradientStart = Color(0xFF3949AB)
val HeroGradientEnd = Color(0xFF7B1FA2)

val BackgroundLight = Color(0xFFF8FAFC)
val SurfaceLight = Color(0xFFFFFFFF)
val TextLight = Color(0xFF1A1C1E)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = PrimaryPurple,
    tertiary = PrimaryPurple,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceDark,
    onPrimary = Color(0xFF1A1C1E),
    onSecondary = Color(0xFF1A1C1E),
    onTertiary = Color(0xFF1A1C1E),
    onBackground = TextDark,
    onSurface = TextDark,
    onSurfaceVariant = TextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = PrimaryPurple,
    tertiary = PrimaryPurple,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextLight,
    onSurface = TextLight,
    onSurfaceVariant = Color(0xFF64748B)
)

@Composable
fun StudyStreakTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
