package io.mns.base.app.ui.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.mns.base.app.data.TodoItem
import io.mns.base.app.data.TodoListSection
import io.mns.base.app.data.TodoRepository
import io.mns.base.app.notifications.ReminderManager
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val application: Application = mockk(relaxed = true)
    private val repository: TodoRepository = mockk(relaxed = true)
    private val reminderManager: ReminderManager = mockk(relaxed = true)
    private val todosLiveData = MutableLiveData<List<TodoItem>>()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { repository.loadTodoItems() } returns todosLiveData

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

        viewModel = HomeViewModel(application)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun sections_groupsItemsIntoHeadersAndItems() {
        val observer = mockk<Observer<List<TodoListSection>>>(relaxed = true)
        viewModel.sections.observeForever(observer)

        val time1 = 1713000000000L
        val item1 = TodoItem("1", time1, "Task 1")
        val item2 = TodoItem("2", time1 + 1000L, "Task 2")
        todosLiveData.value = listOf(item1, item2)

        val captured = slot<List<TodoListSection>>()
        verify { observer.onChanged(capture(captured)) }

        val sections = captured.captured
        assertTrue(sections.isNotEmpty())
        assertTrue(sections.first() is TodoListSection.Header)
        assertEquals(3, sections.size) // 1 Header + 2 Items

        viewModel.sections.removeObserver(observer)
    }

    @Test
    fun insertItem_callsRepositoryInsertTodoItem() = runTest {
        viewModel.insertItem("New Task")
        testScheduler.advanceUntilIdle()

        coVerify(exactly = 1) {
            repository.insertTodoItem(match { it.title == "New Task" })
        }
    }

    @Test
    fun done_callsRepositoryDone() = runTest {
        val item = TodoItem("1", 1000L, "Completed Task")

        viewModel.done(item)
        testScheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.done(item) }
    }

    @Test
    fun addClicked_and_addHandled_updateLiveData() {
        viewModel.addClicked()
        assertEquals(true, viewModel.addClicked.value)

        viewModel.addHandled()
        assertEquals(false, viewModel.addClicked.value)
    }

    @Test
    fun settingClicked_and_settingHandled_updateLiveData() {
        viewModel.settingClicked()
        assertEquals(true, viewModel.settingClicked.value)

        viewModel.settingHandled()
        assertEquals(false, viewModel.settingClicked.value)
    }
}
