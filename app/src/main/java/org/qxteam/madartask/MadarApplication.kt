package org.qxteam.madartask

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.qxteam.madartask.core.database.di.databaseModule
import org.qxteam.madartask.core.model.di.domainModule
import org.qxteam.madartask.feature.display.di.displayModule
import org.qxteam.madartask.feature.input.di.inputModule

class MadarApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MadarApplication)
            modules(
                databaseModule,
                domainModule,
                inputModule,
                displayModule
            )
        }
    }
}
