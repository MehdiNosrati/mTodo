package io.mns.base.app.ui.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.Priority
import io.mns.base.app.data.TodoItem
import io.mns.base.app.data.TodoRepository
import io.mns.base.app.notifications.ReminderManager
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@OptIn(ExperimentalCoroutinesApi::class)
class TodoDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val repository: TodoRepository = mockk(relaxed = true)
    private val reminderManager: ReminderManager = mockk(relaxed = true)

    private lateinit var viewModel: TodoDetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        if (org.koin.core.context.GlobalContext.getOrNull() != null) {
            stopKoin()
        }
        startKoin {
            modules(
                module {
                    single { repository }
                    single { reminderManager }
                }
            )
        }
        viewModel = TodoDetailViewModel(repository, reminderManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun loadTodo_setsTodoItemLiveData() = runTest {
        val todo = TodoItem("t1", 1000L, "Sample Todo", priority = Priority.HIGH)
        coEvery { repository.getTodoById("t1") } returns todo

        viewModel.loadTodo("t1")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(todo, viewModel.todoItem.value)
    }

    @Test
    fun loadDone_setsDoneItemLiveData() = runTest {
        val done = DoneItem("d1", 2000L, "Sample Done Task")
        coEvery { repository.getDoneById("d1") } returns done

        viewModel.loadDone("d1")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(done, viewModel.doneItem.value)
    }

    @Test
    fun saveTodo_createsNewWhenIdIsNull() = runTest {
        var completed = false
        viewModel.saveTodo(
            id = null,
            title = "New Task",
            description = "Some description",
            dueDate = 9999L,
            priority = Priority.MEDIUM,
            tags = listOf("work"),
            onComplete = { completed = true }
        )
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) {
            repository.insertTodoItem(match {
                it.title == "New Task" &&
                it.description == "Some description" &&
                it.priority == Priority.MEDIUM &&
                it.tags == listOf("work")
            })
        }
        assertEquals(true, completed)
    }

    @Test
    fun saveTodo_updatesExistingWhenFound() = runTest {
        val existing = TodoItem("t1", 1000L, "Old Title")
        coEvery { repository.getTodoById("t1") } returns existing
        var completed = false

        viewModel.saveTodo(
            id = "t1",
            title = "Updated Title",
            description = "New description",
            dueDate = null,
            priority = Priority.HIGH,
            tags = listOf("urgent"),
            onComplete = { completed = true }
        )
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) {
            repository.updateTodoItem(match {
                it.id == "t1" &&
                it.title == "Updated Title" &&
                it.priority == Priority.HIGH
            })
        }
        assertEquals(true, completed)
    }

    @Test
    fun completeTodo_delegatesToRepository() = runTest {
        val todo = TodoItem("t1", 1000L, "Todo to finish")
        var completed = false

        viewModel.completeTodo(todo, onComplete = { completed = true })
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.done(todo) }
        assertEquals(true, completed)
    }

    @Test
    fun deleteTodo_delegatesToRepository() = runTest {
        val todo = TodoItem("t1", 1000L, "Todo to delete")
        var completed = false

        viewModel.deleteTodo(todo, onComplete = { completed = true })
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.deleteTodoItem(todo) }
        assertEquals(true, completed)
    }

    @Test
    fun deleteDone_delegatesToRepository() = runTest {
        val done = DoneItem("d1", 2000L, "Done item to delete")
        var completed = false

        viewModel.deleteDone(done, onComplete = { completed = true })
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.deleteDoneItem(done) }
        assertEquals(true, completed)
    }
}
