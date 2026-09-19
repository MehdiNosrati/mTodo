package io.mns.base.app.ui.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.TodoItem
import io.mns.base.app.data.TodoRepository
import io.mns.base.app.data.stats.TaskStatistics
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class InsightsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val application: Application = mockk(relaxed = true)
    private val repository: TodoRepository = mockk(relaxed = true)
    private val todosLiveData = MutableLiveData<List<TodoItem>>()
    private val doneLiveData = MutableLiveData<List<DoneItem>>()

    private lateinit var viewModel: InsightsViewModel

    @Before
    fun setup() {
        if (GlobalContext.getOrNull() != null) {
            stopKoin()
        }
        every { repository.loadTodoItems() } returns todosLiveData
        every { repository.loadDoneItems() } returns doneLiveData

        startKoin {
            modules(
                module {
                    single { repository }
                }
            )
        }

        viewModel = InsightsViewModel(application)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun statistics_observesTodosAndDone_recomputesStatistics() {
        val observer = mockk<Observer<TaskStatistics>>(relaxed = true)
        viewModel.statistics.observeForever(observer)

        val now = System.currentTimeMillis()
        todosLiveData.value = listOf(TodoItem("1", now, "Active task"))
        doneLiveData.value = listOf(DoneItem("d1", now, "Completed task"))

        val captured = mutableListOf<TaskStatistics>()
        verify(atLeast = 1) { observer.onChanged(capture(captured)) }

        val stats = captured.last()
        assertNotNull(stats)
        assertEquals(1, stats.totalActive)
        assertEquals(1, stats.totalDone)
        assertEquals(50f, stats.completionRate, 0.01f)

        viewModel.statistics.removeObserver(observer)
    }
}
