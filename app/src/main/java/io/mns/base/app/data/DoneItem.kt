package io.mns.base.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

val df = SimpleDateFormat("MM/dd", Locale.US)

@Entity(tableName = "doneItems")
data class DoneItem(
    @PrimaryKey val id: String,
    val doneAt: Long,
    val title: String,
    val createdAt: Long = doneAt,
    val description: String = "",
    val dueDate: Long? = null,
    val priority: Priority = Priority.NONE,
    val tags: List<String> = emptyList(),
    val subtasks: List<Subtask> = emptyList(),
    val repeatInterval: RepeatInterval = RepeatInterval.NONE,
    val isPinned: Boolean = false,
    val category: String = "General",
    val deletedAt: Long? = null
) {
    val date: String
        get() = df.format(Date(doneAt))
}
