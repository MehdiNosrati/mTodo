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

    suspend fun done(item: TodoItem) {
        todoDao.done(item)
        doneDao.insert(DoneItem(item.id, Date().time, item.title))
    }
}
