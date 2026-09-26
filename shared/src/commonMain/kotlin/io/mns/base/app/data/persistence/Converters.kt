package io.mns.base.app.data.persistence

import androidx.room.TypeConverter
import io.mns.base.app.data.Priority
import io.mns.base.app.data.RepeatInterval
import io.mns.base.app.data.Subtask
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

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

    @TypeConverter
    fun fromSubtasks(subtasks: List<Subtask>?): String {
        if (subtasks.isNullOrEmpty()) return "[]"
        return json.encodeToString(subtasks)
    }

    @TypeConverter
    fun toSubtasks(data: String?): List<Subtask> {
        if (data.isNullOrBlank()) return emptyList()
        return try {
            json.decodeFromString<List<Subtask>>(data)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromRepeatInterval(interval: RepeatInterval?): String {
        return (interval ?: RepeatInterval.NONE).name
    }

    @TypeConverter
    fun toRepeatInterval(name: String?): RepeatInterval {
        return RepeatInterval.fromName(name)
    }
}
