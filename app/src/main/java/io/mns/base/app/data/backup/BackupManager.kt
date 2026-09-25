package io.mns.base.app.data.backup

import android.content.Context
import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.Priority
import io.mns.base.app.data.RepeatInterval
import io.mns.base.app.data.Subtask
import io.mns.base.app.data.TodoItem
import io.mns.base.app.data.TodoRepository
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
import org.json.JSONArray
import org.json.JSONObject

class BackupManager(
    private val context: Context,
    private val repository: TodoRepository
) {

    suspend fun createBackupJson(): String {
        val todos = repository.getAllTodosList()
        val doneItems = repository.getAllDoneList()

        val root = JSONObject()
        root.put("version", 2)
        root.put("exportedAt", System.currentTimeMillis())

        val todosArray = JSONArray()
        for (item in todos) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("createdAt", item.createdAt)
                put("title", item.title)
                put("description", item.description)
                if (item.dueDate != null) put("dueDate", item.dueDate)
                put("priority", item.priority.level)

                val tagsArr = JSONArray()
                item.tags.forEach { tagsArr.put(it) }
                put("tags", tagsArr)

                val subArr = JSONArray()
                item.subtasks.forEach {
                    val sObj = JSONObject().apply {
                        put("id", it.id)
                        put("title", it.title)
                        put("isDone", it.isDone)
                    }
                    subArr.put(sObj)
                }
                put("subtasks", subArr)
                put("repeatInterval", item.repeatInterval.name)
                put("isPinned", item.isPinned)
                put("category", item.category)
                if (item.deletedAt != null) put("deletedAt", item.deletedAt)
            }
            todosArray.put(obj)
        }
        root.put("todos", todosArray)

        val doneArray = JSONArray()
        for (item in doneItems) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("doneAt", item.doneAt)
                put("createdAt", item.createdAt)
                put("title", item.title)
                put("description", item.description)
                if (item.dueDate != null) put("dueDate", item.dueDate)
                put("priority", item.priority.level)

                val tagsArr = JSONArray()
                item.tags.forEach { tagsArr.put(it) }
                put("tags", tagsArr)

                val subArr = JSONArray()
                item.subtasks.forEach {
                    val sObj = JSONObject().apply {
                        put("id", it.id)
                        put("title", it.title)
                        put("isDone", it.isDone)
                    }
                    subArr.put(sObj)
                }
                put("subtasks", subArr)
                put("repeatInterval", item.repeatInterval.name)
                put("isPinned", item.isPinned)
                put("category", item.category)
                if (item.deletedAt != null) put("deletedAt", item.deletedAt)
            }
            doneArray.put(obj)
        }
        root.put("doneItems", doneArray)

        return root.toString(2)
    }

    suspend fun exportToStream(outputStream: OutputStream) {
        val json = createBackupJson()
        outputStream.bufferedWriter().use { it.write(json) }
    }

    suspend fun restoreFromStream(inputStream: InputStream, overwrite: Boolean = false): Pair<Int, Int> {
        val jsonStr = inputStream.bufferedReader().use { it.readText() }
        val root = JSONObject(jsonStr)

        val todosList = mutableListOf<TodoItem>()
        val todosArray = root.optJSONArray("todos")
        if (todosArray != null) {
            for (i in 0 until todosArray.length()) {
                val obj = todosArray.getJSONObject(i)
                val tagsList = mutableListOf<String>()
                val tagsArr = obj.optJSONArray("tags")
                if (tagsArr != null) {
                    for (t in 0 until tagsArr.length()) {
                        tagsList.add(tagsArr.getString(t))
                    }
                }

                val subtasksList = mutableListOf<Subtask>()
                val subArr = obj.optJSONArray("subtasks")
                if (subArr != null) {
                    for (s in 0 until subArr.length()) {
                        val sObj = subArr.getJSONObject(s)
                        subtasksList.add(
                            Subtask(
                                id = sObj.optString("id", UUID.randomUUID().toString()),
                                title = sObj.optString("title", ""),
                                isDone = sObj.optBoolean("isDone", false)
                            )
                        )
                    }
                }

                todosList.add(
                    TodoItem(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                        title = obj.optString("title", "Untitled"),
                        description = obj.optString("description", ""),
                        dueDate = if (obj.has("dueDate") && !obj.isNull("dueDate")) obj.optLong("dueDate") else null,
                        priority = Priority.fromLevel(obj.optInt("priority", 0)),
                        tags = tagsList,
                        subtasks = subtasksList,
                        repeatInterval = RepeatInterval.fromName(obj.optString("repeatInterval", "NONE")),
                        isPinned = obj.optBoolean("isPinned", false),
                        category = obj.optString("category", "General"),
                        deletedAt = if (obj.has("deletedAt") && !obj.isNull("deletedAt")) obj.optLong("deletedAt") else null
                    )
                )
            }
        }

        val doneList = mutableListOf<DoneItem>()
        val doneArray = root.optJSONArray("doneItems")
        if (doneArray != null) {
            for (i in 0 until doneArray.length()) {
                val obj = doneArray.getJSONObject(i)
                val tagsList = mutableListOf<String>()
                val tagsArr = obj.optJSONArray("tags")
                if (tagsArr != null) {
                    for (t in 0 until tagsArr.length()) {
                        tagsList.add(tagsArr.getString(t))
                    }
                }

                val subtasksList = mutableListOf<Subtask>()
                val subArr = obj.optJSONArray("subtasks")
                if (subArr != null) {
                    for (s in 0 until subArr.length()) {
                        val sObj = subArr.getJSONObject(s)
                        subtasksList.add(
                            Subtask(
                                id = sObj.optString("id", UUID.randomUUID().toString()),
                                title = sObj.optString("title", ""),
                                isDone = sObj.optBoolean("isDone", false)
                            )
                        )
                    }
                }

                doneList.add(
                    DoneItem(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        doneAt = obj.optLong("doneAt", System.currentTimeMillis()),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis()),
                        title = obj.optString("title", "Untitled"),
                        description = obj.optString("description", ""),
                        dueDate = if (obj.has("dueDate") && !obj.isNull("dueDate")) obj.optLong("dueDate") else null,
                        priority = Priority.fromLevel(obj.optInt("priority", 0)),
                        tags = tagsList,
                        subtasks = subtasksList,
                        repeatInterval = RepeatInterval.fromName(obj.optString("repeatInterval", "NONE")),
                        isPinned = obj.optBoolean("isPinned", false),
                        category = obj.optString("category", "General"),
                        deletedAt = if (obj.has("deletedAt") && !obj.isNull("deletedAt")) obj.optLong("deletedAt") else null
                    )
                )
            }
        }

        repository.restoreBackup(todosList, doneList, overwrite)
        return Pair(todosList.size, doneList.size)
    }
}
