package io.mns.base.app.util

import kotlinx.datetime.*

object DateTimeHelper {
    fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()

    fun formatShortDate(timestampMs: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
        val dt = Instant.fromEpochMilliseconds(timestampMs).toLocalDateTime(timeZone)
        val month = dt.monthNumber.toString().padStart(2, '0')
        val day = dt.dayOfMonth.toString().padStart(2, '0')
        return "$month/$day"
    }

    fun truncateToHour(timestampMs: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
        val dt = Instant.fromEpochMilliseconds(timestampMs).toLocalDateTime(timeZone)
        val hourStart = LocalDateTime(dt.year, dt.monthNumber, dt.dayOfMonth, dt.hour, 0, 0, 0)
        return hourStart.toInstant(timeZone).toEpochMilliseconds()
    }

    fun startOfToday(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        return today.atStartOfDayIn(timeZone).toEpochMilliseconds()
    }

    fun endOfToday(timeZone: TimeZone = TimeZone.currentSystemDefault()): Long {
        return startOfToday(timeZone) + 86_400_000L
    }

    fun formatHourRange(hourStartMs: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
        val dt = Instant.fromEpochMilliseconds(hourStartMs).toLocalDateTime(timeZone)
        val today = Clock.System.now().toLocalDateTime(timeZone).date
        val itemDate = dt.date

        val hour = dt.hour
        val startHour12 = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        val startAmPm = if (hour < 12) "AM" else "PM"

        val endHour = (hour + 1) % 24
        val endHour12 = if (endHour == 0) 12 else if (endHour > 12) endHour - 12 else endHour
        val endAmPm = if (endHour < 12) "AM" else "PM"

        val timeRange = "$startHour12:00 $startAmPm – $endHour12:00 $endAmPm"

        val daysDiff = (today.toEpochDays() - itemDate.toEpochDays())
        return when (daysDiff) {
            0 -> "Today · $timeRange"
            1 -> "Yesterday · $timeRange"
            else -> {
                val dayOfWeek = itemDate.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
                val month = itemDate.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
                "$dayOfWeek, $month ${itemDate.dayOfMonth} · $timeRange"
            }
        }
    }
}
