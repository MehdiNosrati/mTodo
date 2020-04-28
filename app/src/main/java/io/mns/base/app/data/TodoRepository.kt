package io.mns.base.app.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import io.mns.base.app.data.persistence.DoneDao
import io.mns.base.app.data.persistence.TodoDao


class TodoRepository(private val todoDao: TodoDao, private val doneDao: DoneDao) {
    suspend fun insert(todo: TodoItem) {
        todoDao.insertTodo(todo)
    }

    fun load(): LiveData<List<TodoItem>?> {
        val mediatorLiveData = MediatorLiveData<List<TodoItem>>()
        mediatorLiveData.addSource(todoDao.getTodos(), mediatorLiveData::postValue)
        return mediatorLiveData
    }

    suspend fun done(item: TodoItem) {
        todoDao.done(item)
    }
}