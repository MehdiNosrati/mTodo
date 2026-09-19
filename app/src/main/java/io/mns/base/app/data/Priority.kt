package io.mns.base.app.data

import androidx.compose.ui.graphics.Color

enum class Priority(
    val label: String,
    val level: Int,
    val colorValue: Long
) {
    NONE("None", 0, 0xFF94A3B8),
    LOW("Low", 1, 0xFF38BDF8),
    MEDIUM("Medium", 2, 0xFFF59E0B),
    HIGH("High", 3, 0xFFEF4444);

    val color: Color
        get() = Color(colorValue)

    companion object {
        fun fromLevel(level: Int): Priority {
            return entries.firstOrNull { it.level == level } ?: NONE
        }

        fun fromName(name: String?): Priority {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: NONE
        }
    }
}
