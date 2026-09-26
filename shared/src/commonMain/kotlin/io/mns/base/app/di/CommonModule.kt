package io.mns.base.app.di

import io.mns.base.app.data.TodoRepository
import io.mns.base.app.data.backup.BackupManager
import io.mns.base.app.ui.viewmodels.*
import org.koin.dsl.module

val commonModule = module {
    single { TodoRepository(get(), get()) }
    single { BackupManager(get()) }

    factory { HomeViewModel(get(), get()) }
    factory { DoneViewModel(get()) }
    factory { InsightsViewModel(get(), get()) }
    factory { SettingViewModel(get(), get(), get(), get()) }
    factory { TodoDetailViewModel(get(), get()) }
}
