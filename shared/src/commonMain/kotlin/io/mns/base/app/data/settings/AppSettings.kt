package io.mns.base.app.data.settings

import io.mns.base.app.data.SortOrder

interface AppSettings {
    fun getSortOrder(): SortOrder
    fun setSortOrder(order: SortOrder)
    fun getDailyGoal(): Int
    fun setDailyGoal(goal: Int)
    fun isDarkMode(): Boolean
    fun setDarkMode(dark: Boolean)
}
