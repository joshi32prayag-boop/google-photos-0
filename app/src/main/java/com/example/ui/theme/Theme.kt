package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AmbientColorScheme = darkColorScheme(
    primary = AmbientPrimary,
    onPrimary = AmbientOnPrimary,
    primaryContainer = AmbientPrimaryContainer,
    onPrimaryContainer = AmbientAccentGold,
    secondary = AmbientSecondary,
    onSecondary = AmbientOnSecondary,
    secondaryContainer = AmbientSecondaryContainer,
    onSecondaryContainer = AmbientSecondary,
    tertiary = AmbientTertiary,
    background = AmbientDarkBackground,
    onBackground = TextPrimary,
    surface = AmbientSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = AmbientSurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AmbientColorScheme,
        typography = Typography,
        content = content
    )
}
