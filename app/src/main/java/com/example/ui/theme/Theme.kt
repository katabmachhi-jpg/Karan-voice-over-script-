package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberDarkColorScheme = darkColorScheme(
    primary = NeonPurple,
    onPrimary = Color.White,
    primaryContainer = NeonPurpleBright,
    onPrimaryContainer = Color.Black,
    secondary = NeonCyan,
    onSecondary = Color.Black,
    secondaryContainer = NeonCyanBright,
    onSecondaryContainer = Color.Black,
    tertiary = NeonMagenta,
    onTertiary = Color.White,
    background = CyberBackground,
    onBackground = TextPrimary,
    surface = CyberBackgroundElevated,
    onSurface = TextPrimary,
    surfaceVariant = CyberCardSurface,
    onSurfaceVariant = TextSecondary,
    outline = CyberCardBorder,
    outlineVariant = CyberCardBorderGlow
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // KARAN VOICE AI is intentionally designed as a premium futuristic dark interface
    MaterialTheme(
        colorScheme = CyberDarkColorScheme,
        typography = Typography,
        content = content
    )
}
