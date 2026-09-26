package io.mns.base.app.ui.tools

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class ToolCategory(
    val title: String,
    val subtitle: String,
    val primaryColor: Color,
    val icon: ImageVector
) {
    MATH(
        title = "Math & Finance",
        subtitle = "Calculators, percentages, loans, units & geometry",
        primaryColor = Color(0xFF6366F1),
        icon = Icons.Default.Calculate
    ),
    TEXT(
        title = "Text & Code",
        subtitle = "Encoders, hashing, ciphers, generators & formatting",
        primaryColor = Color(0xFF06B6D4),
        icon = Icons.Default.Code
    ),
    TIME(
        title = "Time & Date",
        subtitle = "Timers, stopwatches, world clock & calendars",
        primaryColor = Color(0xFFF59E0B),
        icon = Icons.Default.Timer
    ),
    HARDWARE(
        title = "Device & Hardware",
        subtitle = "Sensors, battery, display, audio & diagnostics",
        primaryColor = Color(0xFF10B981),
        icon = Icons.Default.PhoneAndroid
    ),
    EVERYDAY(
        title = "Everyday & Fun",
        subtitle = "Games, dice, coin flip, health & quick tools",
        primaryColor = Color(0xFFEC4899),
        icon = Icons.Default.Casino
    )
}

data class ToolItem(
    val id: Int,
    val name: String,
    val description: String,
    val category: ToolCategory,
    val icon: ImageVector,
    val content: @Composable () -> Unit
)

val ALL_100_TOOLS: List<ToolItem> by lazy {
    MATH_TOOLS + TEXT_TOOLS + TIME_TOOLS + HARDWARE_TOOLS + DAILY_TOOLS
}
