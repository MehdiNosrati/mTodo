package dev.mahdins.core.data.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.mahdins.core.data.model.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoItem)

    @Delete
    suspend fun done(todo: TodoItem)

    @Query("select * from todos order by createdAt desc")
    fun getTodos(): Flow<List<TodoItem>?>
}
