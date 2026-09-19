package io.mns.base.app.data.persistence

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import io.mns.base.app.data.DoneItem
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class DoneDaoIntegrationTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TodoDataBase
    private lateinit var doneDao: DoneDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TodoDataBase::class.java)
            .allowMainThreadQueries()
            .build()
        doneDao = database.doneDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertDoneItem_and_getDoneItems() = runTest {
        val doneItem = DoneItem(id = "d1", doneAt = 2000L, title = "Workout")
        doneDao.insert(doneItem)

        val liveData = doneDao.getDoneItems()
        liveData.observeForever { items ->
            assertEquals(1, items.size)
            assertEquals("Workout", items[0].title)
            assertEquals("d1", items[0].id)
        }
    }
}
