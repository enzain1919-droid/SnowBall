package com.dg479.longrunportfolio.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF007AF5),
    secondary = Color(0xFF9AA4B2),
    tertiary = Color(0xFFFF4F78),
    background = Color(0xFF0F1115),
    surface = Color(0xFF0F1115),
    surfaceVariant = Color(0xFF151922),
    onBackground = Color(0xFFF3F5F8),
    onSurface = Color(0xFFF3F5F8)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF007AF5),
    secondary = Color(0xFF737A84),
    tertiary = Color(0xFFF53B66),
    background = Color.White,
    surface = Color.White,
    surfaceVariant = Color(0xFFF5F6F8),
    onBackground = Color(0xFF111315),
    onSurface = Color(0xFF111315)

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun LongRunPortfolioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> if (darkTheme) DarkColorScheme else LightColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
