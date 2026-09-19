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
}
