package dev.mahdins.mtodo.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.mahdins.mtodo.data.DoneItem
import dev.mahdins.mtodo.data.TodoItem

@Database(version = 2, entities = [TodoItem::class, DoneItem::class], exportSchema = false)
abstract class TodoDataBase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun doneDao(): DoneDao
}
