package io.mns.base.app.ui.viewmodels

import io.mns.base.app.data.SortOrder
import io.mns.base.app.data.TodoItem
import io.mns.base.app.data.TodoRepository
import io.mns.base.app.data.backup.BackupManager
import io.mns.base.app.data.settings.AppSettings
import io.mns.base.app.notifications.ReminderManager
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@OptIn(ExperimentalCoroutinesApi::class)
class SettingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: TodoRepository = mockk(relaxed = true)
    private val reminderManager: ReminderManager = mockk(relaxed = true)
    private val backupManager: BackupManager = mockk(relaxed = true)
    private val settings: AppSettings = mockk(relaxed = true)

    private lateinit var viewModel: SettingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        if (GlobalContext.getOrNull() != null) {
            stopKoin()
        }
        every { settings.getSortOrder() } returns SortOrder.CREATION_DATE_DESC
        every { settings.getDailyGoal() } returns 3

        startKoin {
            modules(
                module {
                    single { repository }
                    single { reminderManager }
                    single { backupManager }
                    single { settings }
                }
            )
        }

        viewModel = SettingViewModel(repository, reminderManager, backupManager, settings)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun backClicked_and_backHandled_updateState() {
        viewModel.backClicked()
        assertEquals(true, viewModel.backClicked.value)

        viewModel.backHandled()
        assertEquals(false, viewModel.backClicked.value)
    }

    @Test
    fun toggleTheme_and_themeToggled_updateState() {
        viewModel.toggleTheme()
        assertEquals(true, viewModel.toggleTheme.value)

        viewModel.themeToggled()
        assertEquals(false, viewModel.toggleTheme.value)
    }

    @Test
    fun setDailyGoal_updatesStateAndPreferences() {
        viewModel.setDailyGoal(5)
        assertEquals(5, viewModel.dailyGoal.value)
        verify(exactly = 1) { settings.setDailyGoal(5) }
    }

    @Test
    fun setSortOrder_updatesStateAndPreferences() {
        viewModel.setSortOrder(SortOrder.DUE_DATE_ASC)
        assertEquals(SortOrder.DUE_DATE_ASC, viewModel.sortOrder.value)
        verify(exactly = 1) { settings.setSortOrder(SortOrder.DUE_DATE_ASC) }
    }

    @Test
    fun restoreTodoFromTrash_delegatesToRepository() = runTest(testDispatcher) {
        val todo = TodoItem("t1", 1000L, "Trashed item")

        viewModel.restoreTodoFromTrash(todo)
        testScheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.restoreTodoItem(todo) }
    }

    @Test
    fun emptyTrash_delegatesToRepository() = runTest(testDispatcher) {
        viewModel.emptyTrash()
        testScheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.emptyTrash() }
    }
}
