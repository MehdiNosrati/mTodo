package io.mns.base.app.data.persistence

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
fun getDatabaseBuilder(): RoomDatabase.Builder<TodoDataBase> {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    val dbFilePath = (documentDirectory?.path ?: "") + "/todo_database.db"
    return Room.databaseBuilder<TodoDataBase>(
        name = dbFilePath
    )
}
