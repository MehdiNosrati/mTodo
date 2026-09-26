package io.mns.base.app.data.backup

import io.mns.base.app.data.*
import io.mns.base.app.util.DateTimeHelper
import io.mns.base.app.util.generateRandomUuid
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class BackupPayload(
    val version: Int = 2,
    val exportedAt: Long,
    val todos: List<BackupTodoItem>,
    val doneItems: List<BackupDoneItem>
)

@Serializable
data class BackupTodoItem(
    val id: String,
    val createdAt: Long,
    val title: String,
    val description: String = "",
    val dueDate: Long? = null,
    val priority: Int = 0,
    val tags: List<String> = emptyList(),
    val subtasks: List<Subtask> = emptyList(),
    val repeatInterval: String = "NONE",
    val isPinned: Boolean = false,
    val category: String = "General",
    val deletedAt: Long? = null
)

@Serializable
data class BackupDoneItem(
    val id: String,
    val doneAt: Long,
    val createdAt: Long = doneAt,
    val title: String,
    val description: String = "",
    val dueDate: Long? = null,
    val priority: Int = 0,
    val tags: List<String> = emptyList(),
    val subtasks: List<Subtask> = emptyList(),
    val repeatInterval: String = "NONE",
    val isPinned: Boolean = false,
    val category: String = "General",
    val deletedAt: Long? = null
)

class BackupManager(private val repository: TodoRepository) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    suspend fun createBackupJson(): String {
        val todos = repository.getAllTodosList()
        val doneItems = repository.getAllDoneList()

        val payload = BackupPayload(
            version = 2,
            exportedAt = DateTimeHelper.currentTimeMillis(),
            todos = todos.map {
                BackupTodoItem(
                    id = it.id,
                    createdAt = it.createdAt,
                    title = it.title,
                    description = it.description,
                    dueDate = it.dueDate,
                    priority = it.priority.level,
                    tags = it.tags,
                    subtasks = it.subtasks,
                    repeatInterval = it.repeatInterval.name,
                    isPinned = it.isPinned,
                    category = it.category,
                    deletedAt = it.deletedAt
                )
            },
            doneItems = doneItems.map {
                BackupDoneItem(
                    id = it.id,
                    doneAt = it.doneAt,
                    createdAt = it.createdAt,
                    title = it.title,
                    description = it.description,
                    dueDate = it.dueDate,
                    priority = it.priority.level,
                    tags = it.tags,
                    subtasks = it.subtasks,
                    repeatInterval = it.repeatInterval.name,
                    isPinned = it.isPinned,
                    category = it.category,
                    deletedAt = it.deletedAt
                )
            }
        )
        return json.encodeToString(payload)
    }

    suspend fun restoreFromJson(jsonStr: String, overwrite: Boolean = false): Pair<Int, Int> {
        val payload = json.decodeFromString<BackupPayload>(jsonStr)

        val todos = payload.todos.map {
            TodoItem(
                id = it.id.ifBlank { generateRandomUuid() },
                createdAt = it.createdAt,
                title = it.title,
                description = it.description,
                dueDate = it.dueDate,
                priority = Priority.fromLevel(it.priority),
                tags = it.tags,
                subtasks = it.subtasks,
                repeatInterval = RepeatInterval.fromName(it.repeatInterval),
                isPinned = it.isPinned,
                category = it.category,
                deletedAt = it.deletedAt
            )
        }

        val doneItems = payload.doneItems.map {
            DoneItem(
                id = it.id.ifBlank { generateRandomUuid() },
                doneAt = it.doneAt,
                createdAt = it.createdAt,
                title = it.title,
                description = it.description,
                dueDate = it.dueDate,
                priority = Priority.fromLevel(it.priority),
                tags = it.tags,
                subtasks = it.subtasks,
                repeatInterval = RepeatInterval.fromName(it.repeatInterval),
                isPinned = it.isPinned,
                category = it.category,
                deletedAt = it.deletedAt
            )
        }

        repository.restoreBackup(todos, doneItems, overwrite)
        return Pair(todos.size, doneItems.size)
    }
}
