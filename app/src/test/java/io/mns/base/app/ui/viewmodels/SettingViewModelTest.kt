package io.mns.base.app.ui.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.SortOrder
import io.mns.base.app.data.TodoItem
import io.mns.base.app.data.TodoRepository
import io.mns.base.app.data.backup.BackupManager
import io.mns.base.app.notifications.ReminderManager
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@OptIn(ExperimentalCoroutinesApi::class)
class SettingViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val application: Application = mockk(relaxed = true)
    private val repository: TodoRepository = mockk(relaxed = true)
    private val reminderManager: ReminderManager = mockk(relaxed = true)
    private val backupManager: BackupManager = mockk(relaxed = true)
    private val sharedPreferences: SharedPreferences = mockk(relaxed = true)
    private val sharedPreferencesEditor: SharedPreferences.Editor = mockk(relaxed = true)

    private lateinit var viewModel: SettingViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        if (GlobalContext.getOrNull() != null) {
            stopKoin()
        }
        startKoin {
            modules(
                module {
                    single { repository }
                    single { reminderManager }
                    single { backupManager }
                }
            )
        }

        every { application.getSharedPreferences("mtodo_settings", Context.MODE_PRIVATE) } returns sharedPreferences
        every { sharedPreferences.edit() } returns sharedPreferencesEditor
        every { sharedPreferencesEditor.putString(any(), any()) } returns sharedPreferencesEditor
        every { sharedPreferencesEditor.putInt(any(), any()) } returns sharedPreferencesEditor
        every { sharedPreferences.getString("pref_sort_order", any()) } returns SortOrder.CREATION_DATE_DESC.name
        every { sharedPreferences.getInt("pref_daily_goal", 3) } returns 3

        viewModel = SettingViewModel(application)
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
        verify(exactly = 1) { sharedPreferencesEditor.putInt("pref_daily_goal", 5) }
    }

    @Test
    fun setSortOrder_updatesStateAndPreferences() {
        viewModel.setSortOrder(SortOrder.DUE_DATE_ASC)
        assertEquals(SortOrder.DUE_DATE_ASC, viewModel.sortOrder.value)
        verify(exactly = 1) { sharedPreferencesEditor.putString("pref_sort_order", SortOrder.DUE_DATE_ASC.name) }
    }

    @Test
    fun restoreTodoFromTrash_delegatesToRepository() = runTest {
        val todo = TodoItem("t1", 1000L, "Trashed item")

        viewModel.restoreTodoFromTrash(todo)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.restoreTodoItem(todo) }
    }

    @Test
    fun emptyTrash_delegatesToRepository() = runTest {
        viewModel.emptyTrash()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.emptyTrash() }
    }
}
