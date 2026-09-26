package io.mns.base.app.data

import io.mns.base.app.util.DateTimeHelper
import kotlinx.datetime.*

enum class RepeatInterval(val label: String) {
    NONE("Does not repeat"),
    DAILY("Daily"),
    WEEKDAYS("Every weekday (Mon–Fri)"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly");

    companion object {
        fun fromName(name: String?): RepeatInterval {
            if (name.isNullOrBlank()) return NONE
            return try {
                valueOf(name)
            } catch (e: Exception) {
                NONE
            }
        }

        fun calculateNextDueDate(
            baseDueDate: Long,
            interval: RepeatInterval,
            timeZone: TimeZone = TimeZone.currentSystemDefault()
        ): Long {
            if (interval == NONE) return baseDueDate

            val now = DateTimeHelper.currentTimeMillis()
            var currentInstant = Instant.fromEpochMilliseconds(baseDueDate)

            while (currentInstant.toEpochMilliseconds() <= now) {
                val ldt = currentInstant.toLocalDateTime(timeZone)
                val nextDate = when (interval) {
                    DAILY -> ldt.date.plus(DatePeriod(days = 1))
                    WEEKDAYS -> {
                        var d = ldt.date.plus(DatePeriod(days = 1))
                        while (d.dayOfWeek == DayOfWeek.SATURDAY || d.dayOfWeek == DayOfWeek.SUNDAY) {
                            d = d.plus(DatePeriod(days = 1))
                        }
                        d
                    }
                    WEEKLY -> ldt.date.plus(DatePeriod(days = 7))
                    MONTHLY -> ldt.date.plus(DatePeriod(months = 1))
                    NONE -> break
                }
                val nextLdt = LocalDateTime(nextDate, ldt.time)
                currentInstant = nextLdt.toInstant(timeZone)
            }

            return currentInstant.toEpochMilliseconds()
        }
    }
}
