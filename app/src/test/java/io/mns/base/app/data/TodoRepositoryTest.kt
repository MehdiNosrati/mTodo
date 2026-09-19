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
        val todo = TodoItem("1", 1000L, "New Item")

        repository.insertTodoItem(todo)

        coVerify(exactly = 1) { todoDao.insertTodo(todo) }
    }

    @Test
    fun done_removesFromTodoDaoAndInsertsToDoneDao() = runTest {
        val todo = TodoItem("1", 1000L, "Finished Item")

        repository.done(todo)

        coVerify(exactly = 1) { todoDao.done(todo) }
        coVerify(exactly = 1) {
            doneDao.insert(match { doneItem ->
                doneItem.title == todo.title
            })
        }
    }
}
