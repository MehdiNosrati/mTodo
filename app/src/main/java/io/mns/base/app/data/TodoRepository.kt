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

    fun loadTrashedTodos(): LiveData<List<TodoItem>> = todoDao.getTrashedTodos()

    fun loadTrashedDoneItems(): LiveData<List<DoneItem>> = doneDao.getTrashedDoneItems()

    suspend fun getTrashedTodosList(): List<TodoItem> = todoDao.getTrashedTodosList()

    suspend fun getTrashedDoneList(): List<DoneItem> = doneDao.getTrashedDoneList()

    suspend fun insertTodoItem(todo: TodoItem) {
        todoDao.insertTodo(todo)
        onDataChanged?.invoke()
    }

    suspend fun updateTodoItem(todo: TodoItem) {
        todoDao.updateTodo(todo)
        onDataChanged?.invoke()
    }

    suspend fun deleteTodoItem(todo: TodoItem) {
        todoDao.softDelete(todo.id)
        onDataChanged?.invoke()
    }

    suspend fun restoreTodoItem(todo: TodoItem) {
        todoDao.restoreFromTrash(todo.id)
        onDataChanged?.invoke()
    }

    suspend fun deleteDoneItem(item: DoneItem) {
        doneDao.softDelete(item.id)
        onDataChanged?.invoke()
    }

    suspend fun restoreDoneItem(item: DoneItem) {
        doneDao.restoreFromTrash(item.id)
        onDataChanged?.invoke()
    }

    suspend fun hardDeleteTodo(id: String) {
        todoDao.hardDelete(id)
        onDataChanged?.invoke()
    }

    suspend fun hardDeleteDone(id: String) {
        doneDao.hardDelete(id)
        onDataChanged?.invoke()
    }

    suspend fun emptyTrash() {
        todoDao.emptyTrash()
        doneDao.emptyTrash()
        onDataChanged?.invoke()
    }

    suspend fun purgeOldTrash(days: Int = 30) {
        val threshold = System.currentTimeMillis() - (days.toLong() * 86_400_000L)
        todoDao.purgeTrashOlderThan(threshold)
        doneDao.purgeTrashOlderThan(threshold)
        onDataChanged?.invoke()
    }

    suspend fun getTodoById(id: String): TodoItem? = todoDao.getTodoById(id)

    suspend fun getDoneById(id: String): DoneItem? = doneDao.getDoneById(id)

    suspend fun done(item: TodoItem): TodoItem? {
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
                subtasks = item.subtasks,
                repeatInterval = item.repeatInterval,
                isPinned = item.isPinned,
                category = item.category
            )
        )

        var nextRecurringItem: TodoItem? = null
        if (item.repeatInterval != RepeatInterval.NONE) {
            val baseTime = item.dueDate ?: System.currentTimeMillis()
            val nextDueDate = RepeatInterval.calculateNextDueDate(baseTime, item.repeatInterval)
            nextRecurringItem = TodoItem(
                id = UUID.randomUUID().toString(),
                createdAt = System.currentTimeMillis(),
                title = item.title,
                description = item.description,
                dueDate = nextDueDate,
                priority = item.priority,
                tags = item.tags,
                subtasks = item.subtasks.map { it.copy(id = UUID.randomUUID().toString(), isDone = false) },
                repeatInterval = item.repeatInterval,
                isPinned = item.isPinned,
                category = item.category
            )
            todoDao.insertTodo(nextRecurringItem)
        }

        onDataChanged?.invoke()
        return nextRecurringItem
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
                subtasks = doneItem.subtasks,
                repeatInterval = doneItem.repeatInterval,
                isPinned = doneItem.isPinned,
                category = doneItem.category
            )
        )
        onDataChanged?.invoke()
    }

    suspend fun batchDone(items: List<TodoItem>): List<TodoItem> {
        val nextItems = mutableListOf<TodoItem>()
        for (item in items) {
            val next = done(item)
            if (next != null) {
                nextItems.add(next)
            }
        }
        return nextItems
    }

    suspend fun batchDelete(items: List<TodoItem>) {
        for (item in items) {
            todoDao.softDelete(item.id)
        }
        onDataChanged?.invoke()
    }

    suspend fun batchSetPin(items: List<TodoItem>, isPinned: Boolean) {
        val updated = items.map { it.copy(isPinned = isPinned) }
        todoDao.updateTodos(updated)
        onDataChanged?.invoke()
    }

    suspend fun batchSetCategory(items: List<TodoItem>, category: String) {
        val updated = items.map { it.copy(category = category) }
        todoDao.updateTodos(updated)
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
