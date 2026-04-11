package io.mns.base.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DarkAccent,
    secondary = CircleColor,
    tertiary = DarkText,
    background = DarkPrimary,
    surface = DarkPrimary,
    onPrimary = DarkPrimary,
    onSecondary = DarkPrimary,
    onTertiary = DarkPrimary,
    onBackground = DarkText,
    onSurface = DarkText,
)

private val LightColorScheme = lightColorScheme(
    primary = Accent,
    secondary = CircleColor,
    tertiary = TextGrey,
    background = Primary,
    surface = Primary,
    onPrimary = Primary,
    onSecondary = Primary,
    onTertiary = Primary,
    onBackground = TextGrey,
    onSurface = TextGrey,
)

@Composable
fun MTodoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
