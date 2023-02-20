package dev.mahdins.core.data.repository

import dev.mahdins.core.data.model.DoneItem
import dev.mahdins.core.data.model.TodoItem
import dev.mahdins.core.data.persistence.DoneDao
import dev.mahdins.core.data.persistence.TodoDao
import kotlinx.coroutines.flow.Flow
import java.util.*

class LocalTodoRepository(private val todoDao: TodoDao, private val doneDao: DoneDao) :
    TodoRepository {

    override suspend fun insertTodoItem(todo: TodoItem) {
        todoDao.insertTodo(todo)
    }

    override fun loadTodoItems(): Flow<List<TodoItem>?> {
        return todoDao.getTodos()
    }

    override fun loadDoneItems(): Flow<List<DoneItem>?> {
        return doneDao.getDoneItems()
    }

    override suspend fun done(item: TodoItem) {
        todoDao.done(item)
        insertDoneItem(DoneItem(item.id, Date().time, item.title))
    }

    private suspend fun insertDoneItem(item: DoneItem) {
        doneDao.insert(item)
    }
}
