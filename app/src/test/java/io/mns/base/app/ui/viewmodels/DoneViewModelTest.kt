package io.mns.base.app.ui.viewmodels

import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.TodoRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@OptIn(ExperimentalCoroutinesApi::class)
class DoneViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: TodoRepository = mockk(relaxed = true)
    private val doneFlow = MutableStateFlow<List<DoneItem>>(emptyList())

    private lateinit var viewModel: DoneViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { repository.loadDoneItems() } returns doneFlow

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

        viewModel = DoneViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        stopKoin()
    }

    @Test
    fun loadItems_returnsDoneItemsFromRepository() = runTest {
        val expected = listOf(DoneItem("1", 1000L, "Finished"))
        doneFlow.value = expected

        val result = viewModel.loadItems().first()

        assertEquals(expected, result)
    }

    @Test
    fun settingClicked_and_settingHandled_updateState() {
        viewModel.settingClicked()
        assertEquals(true, viewModel.settingClicked.value)

        viewModel.settingHandled()
        assertEquals(false, viewModel.settingClicked.value)
    }
}
