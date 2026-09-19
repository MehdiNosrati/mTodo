package io.mns.base.app.data

import androidx.lifecycle.LiveData
import io.mns.base.app.data.persistence.DoneDao
import io.mns.base.app.data.persistence.TodoDao
import java.util.*

class TodoRepository(private val todoDao: TodoDao, private val doneDao: DoneDao) {

    var onDataChanged: (() -> Unit)? = null

    fun loadTodoItems(): LiveData<List<TodoItem>> = todoDao.getTodos()

    fun loadDoneItems(): LiveData<List<DoneItem>> = doneDao.getDoneItems()

    suspend fun getAllTodosList(): List<TodoItem> = todoDao.getAllTodosList()

    suspend fun getAllDoneList(): List<DoneItem> = doneDao.getAllDoneList()

    suspend fun insertTodoItem(todo: TodoItem) {
        todoDao.insertTodo(todo)
        onDataChanged?.invoke()
    }

    suspend fun updateTodoItem(todo: TodoItem) {
        todoDao.updateTodo(todo)
        onDataChanged?.invoke()
    }

    suspend fun deleteTodoItem(todo: TodoItem) {
        todoDao.deleteTodo(todo)
        onDataChanged?.invoke()
    }

    suspend fun restoreTodoItem(todo: TodoItem) {
        todoDao.insertTodo(todo)
        onDataChanged?.invoke()
    }

    suspend fun deleteDoneItem(item: DoneItem) {
        doneDao.delete(item)
        onDataChanged?.invoke()
    }

    suspend fun restoreDoneItem(item: DoneItem) {
        doneDao.insert(item)
        onDataChanged?.invoke()
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
                tags = item.tags,
                subtasks = item.subtasks
            )
        )
        onDataChanged?.invoke()
    }

    suspend fun uncomplete(doneItem: DoneItem) {
        doneDao.delete(doneItem)
        todoDao.insertTodo(
            TodoItem(
                id = doneItem.id,
                createdAt = doneItem.createdAt,
                title = doneItem.title,
                description = doneItem.description,
                dueDate = doneItem.dueDate,
                priority = doneItem.priority,
                tags = doneItem.tags,
                subtasks = doneItem.subtasks
            )
        )
        onDataChanged?.invoke()
    }

    suspend fun restoreBackup(todos: List<TodoItem>, doneItems: List<DoneItem>, overwrite: Boolean = false) {
        if (overwrite) {
            todoDao.clearAll()
            doneDao.clearAll()
        }
        todoDao.insertTodos(todos)
        doneDao.insertDoneItems(doneItems)
        onDataChanged?.invoke()
    }
}
