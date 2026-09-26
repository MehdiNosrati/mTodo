package io.mns.base.app.di

import org.koin.core.context.startKoin

fun doInitKoin() {
    startKoin {
        modules(commonModule, iosModule)
    }
}
