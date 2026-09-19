package io.mns.base.app.data.stats

import io.mns.base.app.data.Priority

data class DayActivity(
    val dayLabel: String,
    val dayName: String,
    val timestampMs: Long,
    val count: Int,
    val isToday: Boolean
)

data class PriorityStat(
    val priority: Priority,
    val activeCount: Int,
    val doneCount: Int
)

data class TaskStatistics(
    val totalActive: Int = 0,
    val totalDone: Int = 0,
    val completionRate: Float = 0f,
    val completedToday: Int = 0,
    val completedThisWeek: Int = 0,
    val currentStreakDays: Int = 0,
    val bestDayOfWeek: String = "None",
    val weeklyActivity: List<DayActivity> = emptyList(),
    val priorityBreakdown: List<PriorityStat> = emptyList(),
    val motivationalTitle: String = "Stay Focused",
    val motivationalMessage: String = "Complete your tasks to build momentum and achieve your goals."
) {
    val totalTasks: Int
        get() = totalActive + totalDone

    val isEmpty: Boolean
        get() = totalTasks == 0
}
