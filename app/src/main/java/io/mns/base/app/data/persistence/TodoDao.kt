package io.mns.base.app.data.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import io.mns.base.app.data.TodoItem

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoItem)

    @Update
    suspend fun updateTodo(todo: TodoItem)

    @Update
    suspend fun updateTodos(todos: List<TodoItem>)

    @Delete
    suspend fun done(todo: TodoItem)

    @Delete
    suspend fun deleteTodo(todo: TodoItem)

    @Delete
    suspend fun deleteTodos(todos: List<TodoItem>)

    @Query("SELECT * FROM todos WHERE id = :id LIMIT 1")
    suspend fun getTodoById(id: String): TodoItem?

    @Query("SELECT * FROM todos WHERE deletedAt IS NULL ORDER BY isPinned DESC, createdAt DESC")
    fun getTodos(): LiveData<List<TodoItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodos(todos: List<TodoItem>)

    @Query("SELECT * FROM todos WHERE deletedAt IS NULL ORDER BY isPinned DESC, createdAt DESC")
    suspend fun getAllTodosList(): List<TodoItem>

    @Query("SELECT * FROM todos WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun getTrashedTodos(): LiveData<List<TodoItem>>

    @Query("SELECT * FROM todos WHERE deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    suspend fun getTrashedTodosList(): List<TodoItem>

    @Query("UPDATE todos SET deletedAt = :deletedAt WHERE id = :id")
    suspend fun softDelete(id: String, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE todos SET deletedAt = NULL WHERE id = :id")
    suspend fun restoreFromTrash(id: String)

    @Query("DELETE FROM todos WHERE id = :id")
    suspend fun hardDelete(id: String)

    @Query("DELETE FROM todos WHERE deletedAt IS NOT NULL AND deletedAt < :olderThanMs")
    suspend fun purgeTrashOlderThan(olderThanMs: Long)

    @Query("DELETE FROM todos WHERE deletedAt IS NOT NULL")
    suspend fun emptyTrash()

    @Query("DELETE FROM todos")
    suspend fun clearAll()
}
