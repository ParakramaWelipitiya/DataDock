package com.datadock.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.datadock.app.utils.PreferencesManager

private val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    secondary = SoftGreen,
    background = PureBlack,
    surface = DarkSurface,
    onPrimary = PureBlack,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00C853), // A deeper green for light mode readability
    secondary = Color(0xFF4CAF50),
    background = Color(0xFFF5F5F5), // Light gray background
    surface = Color.White, // Pure white cards
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun DataDockTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val prefs = PreferencesManager(context)
    val isDarkTheme = prefs.isDarkModeEnabled() // Read the user's choice

    val colorScheme = if (isDarkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Change status bar color based on theme
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}