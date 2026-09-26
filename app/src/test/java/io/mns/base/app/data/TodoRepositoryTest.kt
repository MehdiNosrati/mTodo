package io.mns.base.app.data

import io.mns.base.app.data.persistence.DoneDao
import io.mns.base.app.data.persistence.TodoDao
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class TodoRepositoryTest {

    private val todoDao: TodoDao = mockk(relaxed = true)
    private val doneDao: DoneDao = mockk(relaxed = true)
    private lateinit var repository: TodoRepository

    @Before
    fun setup() {
        repository = TodoRepository(todoDao, doneDao)
    }

    @Test
    fun loadTodoItems_delegatesToTodoDao() {
        val expectedList = listOf(TodoItem("1", 1000L, "Test Todo"))
        val expected = flowOf(expectedList)
        every { todoDao.getTodos() } returns expected

        val result = repository.loadTodoItems()

        assertEquals(expected, result)
        verify(exactly = 1) { todoDao.getTodos() }
    }

    @Test
    fun loadDoneItems_delegatesToDoneDao() {
        val expectedList = listOf(DoneItem("1", 1000L, "Done Task"))
        val expected = flowOf(expectedList)
        every { doneDao.getDoneItems() } returns expected

        val result = repository.loadDoneItems()

        assertEquals(expected, result)
        verify(exactly = 1) { doneDao.getDoneItems() }
    }

    @Test
    fun insertTodoItem_delegatesToTodoDao() = runTest {
        val todo = TodoItem("1", 1000L, "New Item", priority = Priority.HIGH, tags = listOf("work"))

        repository.insertTodoItem(todo)

        coVerify(exactly = 1) { todoDao.insertTodo(todo) }
    }

    @Test
    fun updateTodoItem_delegatesToTodoDao() = runTest {
        val todo = TodoItem("1", 1000L, "Updated Item", priority = Priority.MEDIUM)

        repository.updateTodoItem(todo)

        coVerify(exactly = 1) { todoDao.updateTodo(todo) }
    }

    @Test
    fun deleteTodoItem_softDeletesInTodoDao() = runTest {
        val todo = TodoItem("1", 1000L, "To Delete")

        repository.deleteTodoItem(todo)

        coVerify(exactly = 1) { todoDao.softDelete(eq("1"), any()) }
    }

    @Test
    fun deleteDoneItem_softDeletesInDoneDao() = runTest {
        val done = DoneItem("d1", 2000L, "Finished Task")

        repository.deleteDoneItem(done)

        coVerify(exactly = 1) { doneDao.softDelete(eq("d1"), any()) }
    }

    @Test
    fun restoreTodoItem_restoresInTodoDao() = runTest {
        val todo = TodoItem("1", 1000L, "To Restore")

        repository.restoreTodoItem(todo)

        coVerify(exactly = 1) { todoDao.restoreFromTrash("1") }
    }

    @Test
    fun restoreDoneItem_restoresInDoneDao() = runTest {
        val done = DoneItem("d1", 2000L, "To Restore Done")

        repository.restoreDoneItem(done)

        coVerify(exactly = 1) { doneDao.restoreFromTrash("d1") }
    }

    @Test
    fun emptyTrash_clearsBothDaos() = runTest {
        repository.emptyTrash()

        coVerify(exactly = 1) { todoDao.emptyTrash() }
        coVerify(exactly = 1) { doneDao.emptyTrash() }
    }

    @Test
    fun purgeOldTrash_delegatesThresholdToBothDaos() = runTest {
        repository.purgeOldTrash(30)

        coVerify(exactly = 1) { todoDao.purgeTrashOlderThan(any()) }
        coVerify(exactly = 1) { doneDao.purgeTrashOlderThan(any()) }
    }

    @Test
    fun getTodoById_delegatesToTodoDao() = runTest {
        val expected = TodoItem("123", 1000L, "Specific Todo")
        coEvery { todoDao.getTodoById("123") } returns expected

        val result = repository.getTodoById("123")

        assertEquals(expected, result)
        coVerify(exactly = 1) { todoDao.getTodoById("123") }
    }

    @Test
    fun getDoneById_delegatesToDoneDao() = runTest {
        val expected = DoneItem("d123", 2000L, "Specific Done")
        coEvery { doneDao.getDoneById("d123") } returns expected

        val result = repository.getDoneById("d123")

        assertEquals(expected, result)
        coVerify(exactly = 1) { doneDao.getDoneById("d123") }
    }

    @Test
    fun done_preservesAllMetadataWhenTransferringToDoneDao() = runTest {
        val todo = TodoItem(
            id = "item-99",
            createdAt = 1000L,
            title = "Ship Version 2.5.0",
            description = "Detailed release steps",
            dueDate = 5000000L,
            priority = Priority.HIGH,
            tags = listOf("release", "android"),
            category = "Work"
        )

        val next = repository.done(todo)

        assertNull(next)
        coVerify(exactly = 1) { todoDao.done(todo) }
        coVerify(exactly = 1) {
            doneDao.insert(match { doneItem ->
                doneItem.id == todo.id &&
                doneItem.title == todo.title &&
                doneItem.createdAt == todo.createdAt &&
                doneItem.description == todo.description &&
                doneItem.dueDate == todo.dueDate &&
                doneItem.priority == todo.priority &&
                doneItem.tags == todo.tags &&
                doneItem.category == "Work"
            })
        }
    }

    @Test
    fun done_createsNextOccurrenceWhenRepeating() = runTest {
        val todo = TodoItem(
            id = "item-repeat",
            createdAt = 1000L,
            title = "Daily Standup",
            dueDate = 2000000L,
            repeatInterval = RepeatInterval.DAILY,
            subtasks = listOf(Subtask("s1", "Prepare notes", isDone = true))
        )

        val next = repository.done(todo)

        assertNotNull(next)
        assertEquals(todo.title, next?.title)
        assertEquals(RepeatInterval.DAILY, next?.repeatInterval)
        assertEquals(false, next?.subtasks?.first()?.isDone)
        coVerify(exactly = 1) { todoDao.insertTodo(match { it.title == "Daily Standup" }) }
    }
}
