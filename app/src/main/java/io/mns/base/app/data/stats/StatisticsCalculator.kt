package io.mns.base.app.data.stats

import io.mns.base.app.data.DoneItem
import io.mns.base.app.data.Priority
import io.mns.base.app.data.TodoItem
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

object StatisticsCalculator {

    fun calculate(
        todos: List<TodoItem>,
        doneItems: List<DoneItem>,
        nowMs: Long = System.currentTimeMillis(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): TaskStatistics {
        val totalActive = todos.size
        val totalDone = doneItems.size
        val totalTasks = totalActive + totalDone

        if (totalTasks == 0) {
            return TaskStatistics()
        }

        val completionRate = (totalDone.toFloat() / totalTasks) * 100f

        val today = Instant.ofEpochMilli(nowMs).atZone(zoneId).toLocalDate()

        val doneByDate: Map<LocalDate, Int> = doneItems
            .map { Instant.ofEpochMilli(it.doneAt).atZone(zoneId).toLocalDate() }
            .groupingBy { it }
            .eachCount()

        val completedToday = doneByDate[today] ?: 0

        // 7-day rolling activity
        val weeklyActivity = (6 downTo 0).map { daysAgo ->
            val date = today.minusDays(daysAgo.toLong())
            val count = doneByDate[date] ?: 0
            val dayLabel = date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault())
            val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            val timestampMs = date.atStartOfDay(zoneId).toInstant().toEpochMilli()

            DayActivity(
                dayLabel = dayLabel,
                dayName = dayName,
                timestampMs = timestampMs,
                count = count,
                isToday = (daysAgo == 0)
            )
        }

        val completedThisWeek = weeklyActivity.sumOf { it.count }

        // Streak calculation
        val currentStreakDays = calculateStreak(today, doneByDate)

        // Best day of week
        val bestDayOfWeek = calculateBestDayOfWeek(doneItems, zoneId)

        val (title, message) = generateMotivationalInsight(
            totalActive = totalActive,
            totalDone = totalDone,
            completionRate = completionRate,
            completedToday = completedToday,
            currentStreakDays = currentStreakDays
        )

        // Priority breakdown
        val priorityBreakdown = Priority.entries.map { p ->
            PriorityStat(
                priority = p,
                activeCount = todos.count { it.priority == p },
                doneCount = doneItems.count { it.priority == p }
            )
        }

        return TaskStatistics(
            totalActive = totalActive,
            totalDone = totalDone,
            completionRate = completionRate,
            completedToday = completedToday,
            completedThisWeek = completedThisWeek,
            currentStreakDays = currentStreakDays,
            bestDayOfWeek = bestDayOfWeek,
            weeklyActivity = weeklyActivity,
            priorityBreakdown = priorityBreakdown,
            motivationalTitle = title,
            motivationalMessage = message
        )
    }

    private fun calculateStreak(today: LocalDate, doneByDate: Map<LocalDate, Int>): Int {
        var streak = 0
        val startDay = if ((doneByDate[today] ?: 0) > 0) {
            today
        } else if ((doneByDate[today.minusDays(1)] ?: 0) > 0) {
            today.minusDays(1)
        } else {
            return 0
        }

        var checkDay = startDay
        while ((doneByDate[checkDay] ?: 0) > 0) {
            streak++
            checkDay = checkDay.minusDays(1)
        }
        return streak
    }

    private fun calculateBestDayOfWeek(doneItems: List<DoneItem>, zoneId: ZoneId): String {
        if (doneItems.isEmpty()) return "None"

        val dayCounts = doneItems
            .map { Instant.ofEpochMilli(it.doneAt).atZone(zoneId).toLocalDate().dayOfWeek }
            .groupingBy { it }
            .eachCount()

        val topDay = dayCounts.maxByOrNull { it.value } ?: return "None"
        return topDay.key.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }

    private fun generateMotivationalInsight(
        totalActive: Int,
        totalDone: Int,
        completionRate: Float,
        completedToday: Int,
        currentStreakDays: Int
    ): Pair<String, String> {
        return when {
            totalActive == 0 && totalDone > 0 -> {
                "All Caught Up! 🎉" to "Incredible job! You have completed every task on your list."
            }
            currentStreakDays >= 3 -> {
                "On Fire! 🔥" to "You are on a $currentStreakDays-day streak! Keep up the amazing consistency."
            }
            completedToday > 0 -> {
                val taskWord = if (completedToday == 1) "task" else "tasks"
                "Great Momentum! ⚡" to "You completed $completedToday $taskWord today. Keep pushing forward!"
            }
            completionRate >= 50f -> {
                "Making Progress! 📈" to "You have completed ${completionRate.toInt()}% of your tasks. Keep up the good work!"
            }
            else -> {
                "Stay Focused! 🎯" to "Small steps lead to big accomplishments. Pick one task and finish it today!"
            }
        }
    }
}
