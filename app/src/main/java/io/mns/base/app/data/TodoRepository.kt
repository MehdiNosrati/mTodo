package io.mns.base.app.data

import androidx.lifecycle.LiveData
import io.mns.base.app.data.persistence.TodoDao


class TodoRepository(private val dao: TodoDao) {
    suspend fun insert(todo: TodoItem) {
        dao.insertTodo(todo)
    }

    fun load(): LiveData<List<TodoItem>?> {
        return dao.getTodos()
    }

    suspend fun update(item: TodoItem) {
        dao.updateTodo(item)
    }
}