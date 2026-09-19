package io.mns.base.app.widget

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import io.mns.base.app.R
import io.mns.base.app.data.Priority
import io.mns.base.app.data.TodoItem
import io.mns.base.app.data.TodoRepository
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TodoWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return TodoWidgetFactory(applicationContext)
    }
}

class TodoWidgetFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory, KoinComponent {

    private val repository: TodoRepository by inject()
    private val items = mutableListOf<TodoItem>()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        runBlocking {
            try {
                items.clear()
                items.addAll(repository.getAllTodosList())
            } catch (e: Exception) {
                // Ignore failure during DB access
            }
        }
    }

    override fun onDestroy() {
        items.clear()
    }

    override fun getCount(): Int = items.size

    override fun getViewAt(position: Int): RemoteViews? {
        if (position < 0 || position >= items.size) return null
        val item = items[position]

        val views = RemoteViews(context.packageName, R.layout.widget_todo_item)
        views.setTextViewText(R.id.widget_item_title, item.title)

        // Priority color indicator
        val priorityColor = when (item.priority) {
            Priority.HIGH -> Color.parseColor("#EF4444")
            Priority.MEDIUM -> Color.parseColor("#F59E0B")
            Priority.LOW -> Color.parseColor("#10B981")
            Priority.NONE -> Color.parseColor("#CBD5E1")
        }
        views.setInt(R.id.widget_item_priority, "setBackgroundColor", priorityColor)

        // Subtitle (due date or tags)
        val subtitleParts = mutableListOf<String>()
        if (item.dueDate != null) {
            val isOverdue = item.dueDate < System.currentTimeMillis()
            subtitleParts.add(if (isOverdue) "Overdue" else "Due")
        }
        if (item.tags.isNotEmpty()) {
            subtitleParts.add(item.tags.take(2).joinToString(" ") { "#$it" })
        }
        if (item.subtasks.isNotEmpty()) {
            val doneCount = item.subtasks.count { it.isDone }
            subtitleParts.add("☑ $doneCount/${item.subtasks.size}")
        }

        if (subtitleParts.isNotEmpty()) {
            views.setViewVisibility(R.id.widget_item_subtitle, View.VISIBLE)
            views.setTextViewText(R.id.widget_item_subtitle, subtitleParts.joinToString(" · "))
        } else {
            views.setViewVisibility(R.id.widget_item_subtitle, View.GONE)
        }

        // FillInIntent for item click
        val fillInIntent = Intent().apply {
            putExtra(TodoWidgetProvider.EXTRA_TODO_ID, item.id)
        }
        views.setOnClickFillInIntent(R.id.widget_item_container, fillInIntent)

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = false
}
