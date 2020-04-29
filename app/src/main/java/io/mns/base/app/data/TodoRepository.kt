package io.mns.base.app.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import io.mns.base.app.data.persistence.DoneDao
import io.mns.base.app.data.persistence.TodoDao
import java.util.*


class TodoRepository(private val todoDao: TodoDao, private val doneDao: DoneDao) {
    suspend fun insertTodoItem(todo: TodoItem) {
        todoDao.insertTodo(todo)
    }

    fun loadTodoItems(): LiveData<List<TodoItem>> {
        val mediatorLiveData = MediatorLiveData<List<TodoItem>>()
        mediatorLiveData.addSource(todoDao.getTodos(), mediatorLiveData::postValue)
        return mediatorLiveData
    }

    suspend fun done(item: TodoItem) {
        todoDao.done(item)
        insertDoneItem(DoneItem(item.id, Date().time, item.title))
    }

    private suspend fun insertDoneItem(item: DoneItem) {
        doneDao.insert(item)
    }

    fun loadDoneItems(): LiveData<List<DoneItem>> {
        val mediatorLiveData = MediatorLiveData<List<DoneItem>>()
        mediatorLiveData.addSource(doneDao.getDoneItems(), mediatorLiveData::postValue)
        return mediatorLiveData
    }
}