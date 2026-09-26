package io.mns.base.app.ui.viewmodels

import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.TodoItem
import io.mns.base.app.data.TodoRepository
import io.mns.base.app.data.settings.AppSettings
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@OptIn(ExperimentalCoroutinesApi::class)
class InsightsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: TodoRepository = mockk(relaxed = true)
    private val settings: AppSettings = mockk(relaxed = true)
    private val todosFlow = MutableStateFlow<List<TodoItem>>(emptyList())
    private val doneFlow = MutableStateFlow<List<DoneItem>>(emptyList())

    private lateinit var viewModel: InsightsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        if (GlobalContext.getOrNull() != null) {
            stopKoin()
        }
        every { repository.loadTodoItems() } returns todosFlow
        every { repository.loadDoneItems() } returns doneFlow
        every { settings.getDailyGoal() } returns 3

        startKoin {
            modules(
                module {
                    single { repository }
                    single { settings }
                }
            )
        }

        viewModel = InsightsViewModel(repository, settings)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun statistics_observesTodosAndDone_recomputesStatistics() = runTest(testDispatcher) {
        val now = 1713000000000L
        todosFlow.value = listOf(TodoItem("1", now, "Active task"))
        doneFlow.value = listOf(DoneItem("d1", now, "Completed task"))

        testScheduler.advanceUntilIdle()

        val stats = viewModel.statistics.value
        assertNotNull(stats)
        assertEquals(1, stats.totalActive)
        assertEquals(1, stats.totalDone)
        assertEquals(50f, stats.completionRate, 0.01f)
    }
}
