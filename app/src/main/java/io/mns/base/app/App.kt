package io.mns.base.app

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import io.mns.base.app.di.KoinModules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : MultiDexApplication() {
    private lateinit var preferences: SharedPreferences
    override fun onCreate() {
        super.onCreate()
        startKoin()
        applyTheme()
    }

    private fun applyTheme() {
        preferences = getSharedPreferences(THEME_PREFS_NAME, Context.MODE_PRIVATE)
        val isDark = preferences.getBoolean(IS_DARK, false)
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun startKoin() {
        CoroutineScope(Dispatchers.Default).launch {
            startKoin {
                androidContext(this@App)
                modules(KoinModules.appModule)
            }
        }
    }
}
