package io.mns.base.app.data

import java.util.Calendar

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

        fun calculateNextDueDate(baseDueDate: Long, interval: RepeatInterval): Long {
            if (interval == NONE) return baseDueDate

            val now = System.currentTimeMillis()
            val calendar = Calendar.getInstance().apply {
                timeInMillis = baseDueDate
            }

            while (calendar.timeInMillis <= now) {
                when (interval) {
                    DAILY -> calendar.add(Calendar.DAY_OF_YEAR, 1)
                    WEEKDAYS -> {
                        calendar.add(Calendar.DAY_OF_YEAR, 1)
                        while (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                            calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                        ) {
                            calendar.add(Calendar.DAY_OF_YEAR, 1)
                        }
                    }
                    WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                    MONTHLY -> calendar.add(Calendar.MONTH, 1)
                    NONE -> break
                }
            }

            return calendar.timeInMillis
        }
    }
}
