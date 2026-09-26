package io.mns.base.app.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.mns.base.app.data.persistence.TodoDataBase
import io.mns.base.app.data.persistence.getDatabaseBuilder
import io.mns.base.app.data.settings.AppSettings
import io.mns.base.app.data.settings.IosAppSettings
import io.mns.base.app.notifications.IosReminderManager
import io.mns.base.app.notifications.ReminderManager
import org.koin.dsl.module

val iosModule = module {
    single<TodoDataBase> {
        getDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .addMigrations(*TodoDataBase.ALL_MIGRATIONS)
            .build()
    }
    single { get<TodoDataBase>().todoDao() }
    single { get<TodoDataBase>().doneDao() }
    single<AppSettings> { IosAppSettings() }
    single<ReminderManager> { IosReminderManager() }
}
