package com.bupp.wood_spoon_eaters

import android.app.Application
import com.bupp.wood_spoon_eaters.di.appModule
import com.bupp.wood_spoon_eaters.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class WoodSpoonApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // start Koin context
        startKoin {
            androidContext(this@WoodSpoonApplication)
            androidLogger()
            modules(listOf(appModule, networkModule))
        }

//        LeakSentry.config = LeakSentry.config.copy(watchFragmentViews = true)
    }
}