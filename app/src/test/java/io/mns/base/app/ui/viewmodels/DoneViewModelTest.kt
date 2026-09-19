package io.mns.base.app.ui.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.TodoRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class DoneViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val application: Application = mockk(relaxed = true)
    private val repository: TodoRepository = mockk(relaxed = true)
    private val doneLiveData = MutableLiveData<List<DoneItem>>()

    private lateinit var viewModel: DoneViewModel

    @Before
    fun setup() {
        every { repository.loadDoneItems() } returns doneLiveData

        if (org.koin.core.context.GlobalContext.getOrNull() != null) {
            stopKoin()
        }
        startKoin {
            modules(
                module {
                    single { repository }
                }
            )
        }

        viewModel = DoneViewModel(application)
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun loadItems_returnsDoneItemsFromRepository() {
        val expected = listOf(DoneItem("1", 1000L, "Finished"))
        doneLiveData.value = expected

        val result = viewModel.loadItems()

        assertEquals(expected, result.value)
    }

    @Test
    fun settingClicked_and_settingHandled_updateState() {
        viewModel.settingClicked()
        assertEquals(true, viewModel.settingClicked.value)

        viewModel.settingHandled()
        assertEquals(false, viewModel.settingClicked.value)
    }
}
