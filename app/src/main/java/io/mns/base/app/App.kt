package io.mns.base.app

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import io.mns.base.app.di.KoinModules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        startKoin()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
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