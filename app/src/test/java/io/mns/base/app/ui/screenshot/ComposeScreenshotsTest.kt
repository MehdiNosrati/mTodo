package io.mns.base.app.ui.screenshot

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.lifecycle.MutableLiveData
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.TodoItem
import io.mns.base.app.data.TodoListSection
import io.mns.base.app.data.TodoRepository
import io.mns.base.app.ui.screens.*
import io.mns.base.app.ui.theme.MTodoTheme
import io.mns.base.app.ui.viewmodels.DoneViewModel
import io.mns.base.app.ui.viewmodels.HomeViewModel
import io.mns.base.app.ui.viewmodels.InsightsViewModel
import io.mns.base.app.ui.viewmodels.SettingViewModel
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], qualifiers = RobolectricDeviceQualifiers.Pixel5)
class ComposeScreenshotsTest {

    @get:Rule(order = 0)
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    private val sampleTodos = listOf(
        TodoItem("1", 1713000000000L, "Design new Jetpack Compose UI"),
        TodoItem("2", 1713000000000L + 5000L, "Implement Roborazzi Screenshot tests"),
        TodoItem("3", 1713000000000L + 10000L, "Review pull request changes")
    )

    private val sampleSections = listOf(
        TodoListSection.Header(1713000000000L, "Today · 2:00 PM – 3:00 PM"),
        TodoListSection.Item(sampleTodos[0]),
        TodoListSection.Item(sampleTodos[1]),
        TodoListSection.Item(sampleTodos[2])
    )

    private val sampleDoneItems = listOf(
        DoneItem("d1", 1712900000000L, "Configure Gradle dependencies"),
        DoneItem("d2", 1712800000000L, "Setup Room persistence database")
    )

    @Before
    fun setup() {
        if (GlobalContext.getOrNull() != null) {
            stopKoin()
        }
        val repository = mockk<TodoRepository>(relaxed = true)
        val todosLiveData = androidx.lifecycle.MutableLiveData<List<TodoItem>>(sampleTodos)
        val doneLiveData = androidx.lifecycle.MutableLiveData<List<DoneItem>>(sampleDoneItems)
        every { repository.loadTodoItems() } returns todosLiveData
        every { repository.loadDoneItems() } returns doneLiveData

        startKoin {
            modules(
                module {
                    single { repository }
                    viewModel { HomeViewModel(ApplicationProvider.getApplicationContext()) }
                    viewModel { DoneViewModel(ApplicationProvider.getApplicationContext()) }
                    viewModel { SettingViewModel(ApplicationProvider.getApplicationContext()) }
                    viewModel { InsightsViewModel(ApplicationProvider.getApplicationContext()) }
                }
            )
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testHomeScreenWithItems() {
        composeTestRule.setContent {
            MTodoTheme(darkTheme = false) {
                HomeScreenContent(
                    sections = sampleSections,
                    initialFabVisible = true
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/snapshots/images/home_screen_content.png")
    }

    @Test
    fun testHomeScreenEmpty() {
        composeTestRule.setContent {
            MTodoTheme(darkTheme = false) {
                HomeScreenContent(
                    sections = emptyList(),
                    initialFabVisible = true
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/snapshots/images/home_screen_empty.png")
    }

    @Test
    fun testDoneScreenWithItems() {
        composeTestRule.setContent {
            MTodoTheme(darkTheme = false) {
                DoneScreenContent(
                    items = sampleDoneItems
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/snapshots/images/done_screen_content.png")
    }

    @Test
    fun testDoneScreenEmpty() {
        composeTestRule.setContent {
            MTodoTheme(darkTheme = false) {
                DoneScreenContent(
                    items = emptyList(),
                    animate = false
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/snapshots/images/done_screen_empty.png")
    }

    @Test
    fun testSettingScreen() {
        composeTestRule.setContent {
            MTodoTheme(darkTheme = false) {
                SettingScreenContent(
                    isDark = false,
                    animate = false
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/snapshots/images/setting_screen.png")
    }

    @Test
    fun testInsightsScreen() {
        val sampleStats = io.mns.base.app.data.stats.TaskStatistics(
            totalActive = 3,
            totalDone = 8,
            completionRate = 72.7f,
            completedToday = 2,
            completedThisWeek = 6,
            currentStreakDays = 4,
            bestDayOfWeek = "Thursday",
            weeklyActivity = listOf(
                io.mns.base.app.data.stats.DayActivity("M", "Mon", 0L, 1, false),
                io.mns.base.app.data.stats.DayActivity("T", "Tue", 0L, 0, false),
                io.mns.base.app.data.stats.DayActivity("W", "Wed", 0L, 2, false),
                io.mns.base.app.data.stats.DayActivity("T", "Thu", 0L, 3, false),
                io.mns.base.app.data.stats.DayActivity("F", "Fri", 0L, 1, false),
                io.mns.base.app.data.stats.DayActivity("S", "Sat", 0L, 0, false),
                io.mns.base.app.data.stats.DayActivity("S", "Sun", 0L, 2, true)
            ),
            motivationalTitle = "On Fire! 🔥",
            motivationalMessage = "You are on a 4-day streak! Keep up the amazing consistency."
        )
        composeTestRule.setContent {
            MTodoTheme(darkTheme = false) {
                InsightsScreenContent(
                    statistics = sampleStats,
                    animate = false
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/snapshots/images/insights_screen.png")
    }

    @Test
    fun testInsightsScreenEmpty() {
        composeTestRule.setContent {
            MTodoTheme(darkTheme = false) {
                InsightsScreenContent(
                    statistics = io.mns.base.app.data.stats.TaskStatistics(),
                    animate = false
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/snapshots/images/insights_screen_empty.png")
    }

    @Test
    fun testMainScreen() {
        composeTestRule.setContent {
            MTodoTheme(darkTheme = false) {
                MainScreen(
                    isDark = false,
                    onToggleTheme = {}
                )
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/snapshots/images/main_screen.png")
    }

    @Test
    fun testTodoItemRow() {
        composeTestRule.setContent {
            MTodoTheme(darkTheme = false) {
                Box(modifier = Modifier.padding(16.dp)) {
                    TodoItemRow(
                        item = sampleTodos[0],
                        onDone = {}
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/snapshots/images/item_todo.png")
    }

    @Test
    fun testDoneItemRow() {
        composeTestRule.setContent {
            MTodoTheme(darkTheme = false) {
                Box(modifier = Modifier.padding(16.dp)) {
                    DoneItemRow(
                        item = sampleDoneItems[0]
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/snapshots/images/item_done.png")
    }

    @Test
    fun testTimeSegmentHeader() {
        composeTestRule.setContent {
            MTodoTheme(darkTheme = false) {
                Box(modifier = Modifier.padding(16.dp)) {
                    TimeSegmentHeader(label = "Today · 2:00 PM – 3:00 PM")
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/snapshots/images/time_segment_header.png")
    }

    @Test
    fun testGradientFab() {
        composeTestRule.setContent {
            MTodoTheme(darkTheme = false) {
                Box(modifier = Modifier.padding(16.dp)) {
                    GradientFab(onClick = {})
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/snapshots/images/gradient_fab.png")
    }

    @Test
    fun testAddDialog() {
        composeTestRule.setContent {
            MTodoTheme(darkTheme = false) {
                Box(modifier = Modifier.padding(16.dp)) {
                    AddTodoDialogCard(
                        text = "Buy fresh coffee beans",
                        onTextChange = {},
                        onDismiss = {},
                        onAdd = {}
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage("src/test/snapshots/images/add_dialog.png")
    }
}
