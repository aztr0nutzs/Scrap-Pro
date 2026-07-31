package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = IndustrialOrange,
    onPrimary = Color(0xFF121212),
    secondary = IndustrialBlue,
    onSecondary = Color(0xFFFFFFFF),
    tertiary = SafetyYellow,
    onTertiary = Color(0xFF121212),
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = TextPrimary,
    onSurface = TextSecondary,
    onSurfaceVariant = TextMuted,
    outline = Color(0xFF27272A)
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}
