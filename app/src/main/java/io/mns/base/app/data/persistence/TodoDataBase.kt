package io.mns.base.app.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.TodoItem

@Database(version = 2, entities = [TodoItem::class, DoneItem::class], exportSchema = false)
abstract class TodoDataBase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun doneDao(): DoneDao
}