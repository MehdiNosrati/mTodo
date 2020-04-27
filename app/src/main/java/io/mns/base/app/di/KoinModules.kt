package io.mns.base.app.di

import io.mns.androidlib.NotificationUtil
import org.koin.dsl.module

object KoinModules {
    val appModule = module{
        single {
            NotificationUtil(get())
        }
    }
}