package io.mns.base.app.data.persistence

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.mns.base.app.data.TodoItem
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class TodoDaoIntegrationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TodoDataBase
    private lateinit var todoDao: TodoDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TodoDataBase::class.java)
            .allowMainThreadQueries()
            .build()
        todoDao = database.todoDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertTodo_and_getTodos() = runTest {
        val todo = TodoItem(id = "1", createdAt = 1000L, title = "Buy Milk")
        todoDao.insertTodo(todo)

        val liveData = todoDao.getTodos()
        liveData.observeForever { todos ->
            assertEquals(1, todos.size)
            assertEquals("Buy Milk", todos[0].title)
            assertEquals("1", todos[0].id)
        }
    }

    @Test
    fun done_removesTodo() = runTest {
        val todo = TodoItem(id = "1", createdAt = 1000L, title = "Read Book")
        todoDao.insertTodo(todo)

        todoDao.done(todo)

        val liveData = todoDao.getTodos()
        liveData.observeForever { todos ->
            assertTrue(todos.isEmpty())
        }
    }

    @Test
    fun softDelete_movesTodoToTrash_and_restoreBringsItBack() = runTest {
        val todo = TodoItem(id = "2", createdAt = 2000L, title = "Write Tests")
        todoDao.insertTodo(todo)

        todoDao.softDelete("2")

        val activeList = todoDao.getAllTodosList()
        assertTrue(activeList.none { it.id == "2" })

        val trashedList = todoDao.getTrashedTodosList()
        assertEquals(1, trashedList.size)
        assertEquals("2", trashedList[0].id)

        todoDao.restoreFromTrash("2")
        val restoredActive = todoDao.getAllTodosList()
        assertEquals(1, restoredActive.size)
        assertEquals("2", restoredActive[0].id)
    }

    @Test
    fun emptyTrash_removesSoftDeletedItems() = runTest {
        val todo = TodoItem(id = "3", createdAt = 3000L, title = "Old Task")
        todoDao.insertTodo(todo)
        todoDao.softDelete("3")

        todoDao.emptyTrash()

        val trashedList = todoDao.getTrashedTodosList()
        assertTrue(trashedList.isEmpty())
    }
}
