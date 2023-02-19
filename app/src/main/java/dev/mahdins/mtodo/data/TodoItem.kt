package dev.mahdins.mtodo.data


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class TodoItem(
    @PrimaryKey val id: String,
    val createdAt: Long,
    val title: String
)
