package io.mns.base.app.data.persistence

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.TodoItem

@Database(version = 5, entities = [TodoItem::class, DoneItem::class], exportSchema = false)
@TypeConverters(Converters::class)
@ConstructedBy(TodoDataBaseConstructor::class)
abstract class TodoDataBase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun doneDao(): DoneDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE todos ADD COLUMN description TEXT NOT NULL DEFAULT ''")
                connection.execSQL("ALTER TABLE todos ADD COLUMN dueDate INTEGER DEFAULT NULL")
                connection.execSQL("ALTER TABLE todos ADD COLUMN priority INTEGER NOT NULL DEFAULT 0")
                connection.execSQL("ALTER TABLE todos ADD COLUMN tags TEXT NOT NULL DEFAULT ''")

                connection.execSQL("ALTER TABLE doneItems ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
                connection.execSQL("ALTER TABLE doneItems ADD COLUMN description TEXT NOT NULL DEFAULT ''")
                connection.execSQL("ALTER TABLE doneItems ADD COLUMN dueDate INTEGER DEFAULT NULL")
                connection.execSQL("ALTER TABLE doneItems ADD COLUMN priority INTEGER NOT NULL DEFAULT 0")
                connection.execSQL("ALTER TABLE doneItems ADD COLUMN tags TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE todos ADD COLUMN subtasks TEXT NOT NULL DEFAULT '[]'")
                connection.execSQL("ALTER TABLE doneItems ADD COLUMN subtasks TEXT NOT NULL DEFAULT '[]'")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE todos ADD COLUMN repeatInterval TEXT NOT NULL DEFAULT 'NONE'")
                connection.execSQL("ALTER TABLE todos ADD COLUMN isPinned INTEGER NOT NULL DEFAULT 0")
                connection.execSQL("ALTER TABLE todos ADD COLUMN category TEXT NOT NULL DEFAULT 'General'")
                connection.execSQL("ALTER TABLE todos ADD COLUMN deletedAt INTEGER DEFAULT NULL")

                connection.execSQL("ALTER TABLE doneItems ADD COLUMN repeatInterval TEXT NOT NULL DEFAULT 'NONE'")
                connection.execSQL("ALTER TABLE doneItems ADD COLUMN isPinned INTEGER NOT NULL DEFAULT 0")
                connection.execSQL("ALTER TABLE doneItems ADD COLUMN category TEXT NOT NULL DEFAULT 'General'")
                connection.execSQL("ALTER TABLE doneItems ADD COLUMN deletedAt INTEGER DEFAULT NULL")
            }
        }

        val ALL_MIGRATIONS = arrayOf(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
    }
}

@Suppress("KotlinNoActualForExpect")
expect object TodoDataBaseConstructor : RoomDatabaseConstructor<TodoDataBase> {
    override fun initialize(): TodoDataBase
}
