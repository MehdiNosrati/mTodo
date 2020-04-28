package io.mns.base.app.data.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.mns.base.app.data.TodoItem

@Dao
interface TodoDao {
    @Insert
    suspend fun insertTodo(todo: TodoItem)

    @Update
    suspend fun updateTodo(todo: TodoItem)

    @Query("select * from todos")
    fun getTodos(): LiveData<List<TodoItem>?>
}