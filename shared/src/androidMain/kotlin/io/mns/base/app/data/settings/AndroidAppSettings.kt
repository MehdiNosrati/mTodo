package io.mns.base.app.data.settings

import android.content.Context
import android.content.SharedPreferences
import io.mns.base.app.data.SortOrder

class AndroidAppSettings(context: Context) : AppSettings {
    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences("mtodo_settings", Context.MODE_PRIVATE)

    override fun getSortOrder(): SortOrder {
        val name = prefs.getString("pref_sort_order", SortOrder.CREATION_DATE_DESC.name) ?: SortOrder.CREATION_DATE_DESC.name
        return try {
            SortOrder.valueOf(name)
        } catch (e: Exception) {
            SortOrder.CREATION_DATE_DESC
        }
    }

    override fun setSortOrder(order: SortOrder) {
        prefs.edit().putString("pref_sort_order", order.name).apply()
    }

    override fun getDailyGoal(): Int {
        return prefs.getInt("pref_daily_goal", 3)
    }

    override fun setDailyGoal(goal: Int) {
        prefs.edit().putInt("pref_daily_goal", goal).apply()
    }

    override fun isDarkMode(): Boolean {
        return prefs.getBoolean("pref_dark_mode", false)
    }

    override fun setDarkMode(dark: Boolean) {
        prefs.edit().putBoolean("pref_dark_mode", dark).apply()
    }
}
