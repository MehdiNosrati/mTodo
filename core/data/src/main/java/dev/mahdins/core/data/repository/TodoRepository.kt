package dev.mahdins.core.data.repository

import androidx.lifecycle.LiveData
import dev.mahdins.core.data.model.DoneItem
import dev.mahdins.core.data.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    suspend fun insertTodoItem(todo: TodoItem)

    suspend fun done(item: TodoItem)

    fun loadTodoItems(): Flow<List<TodoItem>?>

    fun loadDoneItems(): Flow<List<DoneItem>?>
}