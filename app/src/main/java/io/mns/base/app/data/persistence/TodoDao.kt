package io.mns.base.app.data.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import io.mns.base.app.data.TodoItem

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoItem)

    @Delete
    suspend fun done(todo: TodoItem)

    @Query("select * from todos order by createdAt desc")
    fun getTodos(): LiveData<List<TodoItem>?>
}
