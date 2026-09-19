package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GameDarkColorScheme = darkColorScheme(
    primary = TrainYellowPrimary,
    onPrimary = TrainCharcoal,
    primaryContainer = TrainYellowDark,
    onPrimaryContainer = Color.White,
    secondary = TrainBrightCyan,
    onSecondary = TrainCharcoal,
    secondaryContainer = TrainSlate,
    onSecondaryContainer = Color.White,
    tertiary = TrainGreen,
    onTertiary = Color.White,
    background = TrainCharcoal,
    onBackground = Color.White,
    surface = TrainDarkSteel,
    onSurface = Color.White,
    surfaceVariant = TrainSlate,
    onSurfaceVariant = TrainLightGray,
    error = TrainSafetyRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GameDarkColorScheme,
        typography = Typography,
        content = content
    )
}

