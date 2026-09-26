package io.mns.base.app.data.persistence

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<TodoDataBase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("todo_database.db")
    return Room.databaseBuilder<TodoDataBase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
