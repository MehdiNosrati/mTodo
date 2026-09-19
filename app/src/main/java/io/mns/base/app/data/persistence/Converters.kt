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

    @TypeConverter
    fun fromSubtasks(subtasks: List<io.mns.base.app.data.Subtask>?): String {
        if (subtasks.isNullOrEmpty()) return "[]"
        val array = org.json.JSONArray()
        for (item in subtasks) {
            val obj = org.json.JSONObject()
            obj.put("id", item.id)
            obj.put("title", item.title)
            obj.put("isDone", item.isDone)
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toSubtasks(data: String?): List<io.mns.base.app.data.Subtask> {
        if (data.isNullOrBlank()) return emptyList()
        return try {
            val array = org.json.JSONArray(data)
            val list = mutableListOf<io.mns.base.app.data.Subtask>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    io.mns.base.app.data.Subtask(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        title = obj.optString("title", ""),
                        isDone = obj.optBoolean("isDone", false)
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }
}
