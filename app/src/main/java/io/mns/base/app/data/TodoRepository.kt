package io.mns.base.app.data

import androidx.lifecycle.LiveData
import io.mns.base.app.data.persistence.DoneDao
import io.mns.base.app.data.persistence.TodoDao
import java.util.*

class TodoRepository(private val todoDao: TodoDao, private val doneDao: DoneDao) {

    fun loadTodoItems(): LiveData<List<TodoItem>> = todoDao.getTodos()

    fun loadDoneItems(): LiveData<List<DoneItem>> = doneDao.getDoneItems()

    suspend fun insertTodoItem(todo: TodoItem) {
        todoDao.insertTodo(todo)
    }

    suspend fun updateTodoItem(todo: TodoItem) {
        todoDao.updateTodo(todo)
    }

    suspend fun deleteTodoItem(todo: TodoItem) {
        todoDao.deleteTodo(todo)
    }

    suspend fun deleteDoneItem(item: DoneItem) {
        doneDao.delete(item)
    }

    suspend fun getTodoById(id: String): TodoItem? = todoDao.getTodoById(id)

    suspend fun getDoneById(id: String): DoneItem? = doneDao.getDoneById(id)

    suspend fun done(item: TodoItem) {
        todoDao.done(item)
        doneDao.insert(
            DoneItem(
                id = item.id,
                doneAt = System.currentTimeMillis(),
                title = item.title,
                createdAt = item.createdAt,
                description = item.description,
                dueDate = item.dueDate,
                priority = item.priority,
                tags = item.tags
            )
        )
    }
}
