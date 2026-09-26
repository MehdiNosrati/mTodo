package io.mns.base.app.notifications

import io.mns.base.app.data.TodoItem
import io.mns.base.app.util.DateTimeHelper
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

class IosReminderManager : ReminderManager {
    override fun scheduleReminder(item: TodoItem) {
        val dueTime = item.dueDate ?: return
        val remainingMs = dueTime - DateTimeHelper.currentTimeMillis()
        val triggerInterval = remainingMs / 1000.0
        if (triggerInterval <= 0) return

        val center = UNUserNotificationCenter.currentNotificationCenter()
        val content = UNMutableNotificationContent().apply {
            setTitle(item.title)
            if (item.description.isNotBlank()) {
                setBody(item.description)
            }
            setSound(UNNotificationSound.defaultSound())
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(triggerInterval, repeats = false)
        val request = UNNotificationRequest.requestWithIdentifier(item.id, content, trigger)

        center.addNotificationRequest(request, null)
    }

    override fun cancelReminder(todoId: String) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.removePendingNotificationRequestsWithIdentifiers(listOf(todoId))
    }

    override fun areNotificationsEnabled(): Boolean {
        return true
    }
}
