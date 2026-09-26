package io.mns.base.app.notifications

import io.mns.base.app.data.TodoItem

interface ReminderManager {
    fun scheduleReminder(item: TodoItem)
    fun cancelReminder(todoId: String)
    fun areNotificationsEnabled(): Boolean
}
