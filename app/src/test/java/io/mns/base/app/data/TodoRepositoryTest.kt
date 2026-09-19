package io.mns.base.app.data

import androidx.lifecycle.MutableLiveData
import io.mns.base.app.data.persistence.DoneDao
import io.mns.base.app.data.persistence.TodoDao
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TodoRepositoryTest {

    private val todoDao: TodoDao = mockk(relaxed = true)
    private val doneDao: DoneDao = mockk(relaxed = true)
    private lateinit var repository: TodoRepository

    @Before
    fun setup() {
        repository = TodoRepository(todoDao, doneDao)
    }

    @Test
    fun loadTodoItems_delegatesToTodoDao() {
        val expected = MutableLiveData<List<TodoItem>>(listOf(TodoItem("1", 1000L, "Test Todo")))
        every { todoDao.getTodos() } returns expected

        val result = repository.loadTodoItems()

        assertEquals(expected, result)
        verify(exactly = 1) { todoDao.getTodos() }
    }

    @Test
    fun loadDoneItems_delegatesToDoneDao() {
        val expected = MutableLiveData<List<DoneItem>>(listOf(DoneItem("1", 1000L, "Done Task")))
        every { doneDao.getDoneItems() } returns expected

        val result = repository.loadDoneItems()

        assertEquals(expected, result)
        verify(exactly = 1) { doneDao.getDoneItems() }
    }

    @Test
    fun insertTodoItem_delegatesToTodoDao() = runTest {
        val todo = TodoItem("1", 1000L, "New Item", priority = Priority.HIGH, tags = listOf("work"))

        repository.insertTodoItem(todo)

        coVerify(exactly = 1) { todoDao.insertTodo(todo) }
    }

    @Test
    fun updateTodoItem_delegatesToTodoDao() = runTest {
        val todo = TodoItem("1", 1000L, "Updated Item", priority = Priority.MEDIUM)

        repository.updateTodoItem(todo)

        coVerify(exactly = 1) { todoDao.updateTodo(todo) }
    }

    @Test
    fun deleteTodoItem_delegatesToTodoDao() = runTest {
        val todo = TodoItem("1", 1000L, "To Delete")

        repository.deleteTodoItem(todo)

        coVerify(exactly = 1) { todoDao.deleteTodo(todo) }
    }

    @Test
    fun deleteDoneItem_delegatesToDoneDao() = runTest {
        val done = DoneItem("d1", 2000L, "Finished Task")

        repository.deleteDoneItem(done)

        coVerify(exactly = 1) { doneDao.delete(done) }
    }

    @Test
    fun getTodoById_delegatesToTodoDao() = runTest {
        val expected = TodoItem("123", 1000L, "Specific Todo")
        coEvery { todoDao.getTodoById("123") } returns expected

        val result = repository.getTodoById("123")

        assertEquals(expected, result)
        coVerify(exactly = 1) { todoDao.getTodoById("123") }
    }

    @Test
    fun getDoneById_delegatesToDoneDao() = runTest {
        val expected = DoneItem("d123", 2000L, "Specific Done")
        coEvery { doneDao.getDoneById("d123") } returns expected

        val result = repository.getDoneById("d123")

        assertEquals(expected, result)
        coVerify(exactly = 1) { doneDao.getDoneById("d123") }
    }

    @Test
    fun done_preservesAllMetadataWhenTransferringToDoneDao() = runTest {
        val todo = TodoItem(
            id = "item-99",
            createdAt = 1000L,
            title = "Ship Version 2.2.0",
            description = "Detailed release steps",
            dueDate = 5000000L,
            priority = Priority.HIGH,
            tags = listOf("release", "android")
        )

        repository.done(todo)

        coVerify(exactly = 1) { todoDao.done(todo) }
        coVerify(exactly = 1) {
            doneDao.insert(match { doneItem ->
                doneItem.id == todo.id &&
                doneItem.title == todo.title &&
                doneItem.createdAt == todo.createdAt &&
                doneItem.description == todo.description &&
                doneItem.dueDate == todo.dueDate &&
                doneItem.priority == todo.priority &&
                doneItem.tags == todo.tags
            })
        }
    }
}
