package io.mns.base.app.ui.viewmodels

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val application: Application = mockk(relaxed = true)
    private lateinit var viewModel: SettingViewModel

    @Before
    fun setup() {
        viewModel = SettingViewModel(application)
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
}
