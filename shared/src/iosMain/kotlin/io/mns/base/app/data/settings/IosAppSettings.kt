package io.mns.base.app.data.settings

import io.mns.base.app.data.SortOrder
import platform.Foundation.NSUserDefaults

class IosAppSettings : AppSettings {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun getSortOrder(): SortOrder {
        val name = defaults.stringForKey("pref_sort_order") ?: SortOrder.CREATION_DATE_DESC.name
        return try {
            SortOrder.valueOf(name)
        } catch (e: Exception) {
            SortOrder.CREATION_DATE_DESC
        }
    }

    override fun setSortOrder(order: SortOrder) {
        defaults.setObject(order.name, "pref_sort_order")
    }

    override fun getDailyGoal(): Int {
        val goal = defaults.integerForKey("pref_daily_goal")
        return if (goal <= 0) 3 else goal.toInt()
    }

    override fun setDailyGoal(goal: Int) {
        defaults.setInteger(goal.toLong(), "pref_daily_goal")
    }

    override fun isDarkMode(): Boolean {
        return defaults.boolForKey("pref_dark_mode")
    }

    override fun setDarkMode(dark: Boolean) {
        defaults.setBool(dark, "pref_dark_mode")
    }
}
