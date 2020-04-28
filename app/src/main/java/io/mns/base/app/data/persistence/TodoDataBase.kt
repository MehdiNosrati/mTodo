package io.mns.base.app.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import io.mns.base.app.data.TodoItem

@Database(version = 1, entities = [TodoItem::class], exportSchema = false)
abstract class TodoDataBase: RoomDatabase() {
    abstract fun todoDao(): TodoDao
}