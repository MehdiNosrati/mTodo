package io.mns.base.app.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import io.mns.base.app.data.persistence.DoneDao
import io.mns.base.app.data.persistence.TodoDao
import java.util.*


class TodoRepository(private val todoDao: TodoDao, private val doneDao: DoneDao) {
    private val todoMediator = MediatorLiveData<List<TodoItem>>()
    private val doneMediator = MediatorLiveData<List<DoneItem>>()
    suspend fun insertTodoItem(todo: TodoItem) {
        todoDao.insertTodo(todo)
    }

    fun loadTodoItems(): LiveData<List<TodoItem>> {
        todoMediator.addSource(todoDao.getTodos(), todoMediator::postValue)
        return todoMediator
    }

    suspend fun done(item: TodoItem) {
        todoDao.done(item)
        insertDoneItem(DoneItem(item.id, Date().time, item.title))
    }

    private suspend fun insertDoneItem(item: DoneItem) {
        doneDao.insert(item)
    }

    fun loadDoneItems(): LiveData<List<DoneItem>> {
        doneMediator.addSource(doneDao.getDoneItems(), doneMediator::postValue)
        return doneMediator
    }
}