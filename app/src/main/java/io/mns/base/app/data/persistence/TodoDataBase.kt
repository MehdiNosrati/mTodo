package io.mns.base.app.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.TodoItem

@Database(version = 3, entities = [TodoItem::class, DoneItem::class], exportSchema = false)
@TypeConverters(Converters::class)
abstract class TodoDataBase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun doneDao(): DoneDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE todos ADD COLUMN description TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE todos ADD COLUMN dueDate INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE todos ADD COLUMN priority INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE todos ADD COLUMN tags TEXT NOT NULL DEFAULT ''")

                db.execSQL("ALTER TABLE doneItems ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE doneItems ADD COLUMN description TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE doneItems ADD COLUMN dueDate INTEGER DEFAULT NULL")
                db.execSQL("ALTER TABLE doneItems ADD COLUMN priority INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE doneItems ADD COLUMN tags TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}
