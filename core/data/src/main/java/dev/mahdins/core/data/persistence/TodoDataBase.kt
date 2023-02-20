package dev.mahdins.core.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    version = 2,
    entities = [dev.mahdins.core.data.model.TodoItem::class,
        dev.mahdins.core.data.model.DoneItem::class],
    exportSchema = false
)
abstract class TodoDataBase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun doneDao(): DoneDao
}
