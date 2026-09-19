package io.mns.base.app.data.persistence

import androidx.room.TypeConverter
import io.mns.base.app.data.Priority

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority?): Int {
        return priority?.level ?: Priority.NONE.level
    }

    @TypeConverter
    fun toPriority(level: Int?): Priority {
        return Priority.fromLevel(level ?: 0)
    }

    @TypeConverter
    fun fromTags(tags: List<String>?): String {
        return tags?.filter { it.isNotBlank() }?.joinToString(separator = ",") ?: ""
    }

    @TypeConverter
    fun toTags(data: String?): List<String> {
        if (data.isNullOrBlank()) return emptyList()
        return data.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
}
