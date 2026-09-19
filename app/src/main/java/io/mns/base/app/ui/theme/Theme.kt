package io.mns.base.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = Indigo400,
    onPrimary = DarkText,
    secondary = Violet400,
    onSecondary = DarkText,
    tertiary = Emerald500,
    background = DarkBg,
    surface = DarkSurface,
    surfaceVariant = DarkCard,
    onBackground = DarkText,
    onSurface = DarkText,
    onSurfaceVariant = DarkMuted,
    outline = DarkOutline,
    outlineVariant = DarkOutline,
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo500,
    onPrimary = LightSurface,
    secondary = Violet400,
    onSecondary = LightSurface,
    tertiary = Emerald500,
    background = LightBg,
    surface = LightSurface,
    surfaceVariant = LightBg,
    onBackground = LightText,
    onSurface = LightText,
    onSurfaceVariant = LightMuted,
    outline = LightOutline,
    outlineVariant = LightOutline,
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun MTodoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        shapes = AppShapes,
        content = content
    )
}
