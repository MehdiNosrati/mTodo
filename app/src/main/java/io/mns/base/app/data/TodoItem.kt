package io.mns.base.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoItem(
    @PrimaryKey val id: String,
    val createdAt: Long,
    val title: String,
    val description: String = "",
    val dueDate: Long? = null,
    val priority: Priority = Priority.NONE,
    val tags: List<String> = emptyList()
)
