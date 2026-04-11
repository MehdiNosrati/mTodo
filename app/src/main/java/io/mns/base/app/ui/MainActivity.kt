package io.mns.base.app.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.mns.androidlib.isDark
import io.mns.base.app.IS_DARK
import io.mns.base.app.THEME_PREFS_NAME
import io.mns.base.app.ui.screens.MainScreen
import io.mns.base.app.ui.theme.MTodoTheme
import androidx.core.content.edit

class MainActivity : ComponentActivity() {
    fun showBottomNav() {}
    fun hideBottomNav() {}
    fun toggleTheme() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences(THEME_PREFS_NAME, Context.MODE_PRIVATE)

        setContent {
            var isDark by remember {
                mutableStateOf(sharedPreferences.getBoolean(IS_DARK, resources.isDark()))
            }

            MTodoTheme(darkTheme = isDark) {
                MainScreen(onToggleTheme = {
                    isDark = !isDark
                    sharedPreferences.edit { putBoolean(IS_DARK, isDark) }
                })
            }
        }
    }
}
