package dev.mahdins.mtodo.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import dev.mahdins.mtodo.data.persistence.DoneDao
import dev.mahdins.mtodo.data.persistence.TodoDao
import java.util.*

class TodoRepository(private val todoDao: TodoDao, private val doneDao: DoneDao) {
    private val todoMediator = MediatorLiveData<List<TodoItem>>()
    private val doneMediator = MediatorLiveData<List<DoneItem>>()

    init {
        todoMediator.addSource(todoDao.getTodos(), todoMediator::postValue)
        doneMediator.addSource(doneDao.getDoneItems(), doneMediator::postValue)
    }

    suspend fun insertTodoItem(todo: TodoItem) {
        todoDao.insertTodo(todo)
    }

    fun loadTodoItems(): LiveData<List<TodoItem>> {
        return todoMediator
    }

    fun loadDoneItems(): LiveData<List<DoneItem>> {
        return doneMediator
    }

    suspend fun done(item: TodoItem) {
        todoDao.done(item)
        insertDoneItem(DoneItem(item.id, Date().time, item.title))
    }

    private suspend fun insertDoneItem(item: DoneItem) {
        doneDao.insert(item)
    }
}
