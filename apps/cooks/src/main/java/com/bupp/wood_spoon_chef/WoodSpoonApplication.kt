package com.bupp.wood_spoon_chef

import android.app.Application
import com.bupp.wood_spoon_chef.common.MTLogger
import com.bupp.wood_spoon_chef.di.appModule
import com.bupp.wood_spoon_chef.di.networkModule
import com.bupp.wood_spoon_chef.presentation.features.cooking_slot.module.cookingSlotModule
import com.eatwoodspoon.analytics.analyticsModule
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import com.segment.analytics.Analytics
import com.segment.analytics.android.integrations.appsflyer.AppsflyerIntegration
import com.segment.analytics.android.integrations.mixpanel.MixpanelIntegration
import com.uxcam.UXCam
import io.branch.referral.Branch
import io.shipbook.shipbooksdk.ShipBook
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


class WoodSpoonApplication : Application() {

    companion object {
        private lateinit var instance: WoodSpoonApplication

        fun getInstance(): WoodSpoonApplication = instance
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        initShipBook()

        // start Koin context
        startKoin {
            androidContext(this@WoodSpoonApplication)
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            modules(appModule + cookingSlotModule + networkModule + analyticsModule)
        }

        AppCenter.start(
            this,
            "906c970f-76e7-46df-8b07-af61ed082931",
            com.microsoft.appcenter.analytics.Analytics::class.java,
            Crashes::class.java,
            Distribute::class.java
        )

        Branch.enableLogging()
        Branch.getAutoInstance(this)

        MTLogger.Builder()
            .showLinks(true)
            .autoTag(true)
            .tagPrefix("MTL")
            .build()

        UXCam.startWithKey(BuildConfig.UX_CAM_APP_KEY)

        val analytics = Analytics.Builder(
            applicationContext,
            BuildConfig.SEGMENT_KEY
        ) // Enable this to record certain application events automatically!
            .trackApplicationLifecycleEvents() // Enable this to record screen views automatically!
            .recordScreenViews()
            .logLevel(Analytics.LogLevel.VERBOSE)
            .use(MixpanelIntegration.FACTORY)
            .use(AppsflyerIntegration.FACTORY)
            .build()
        Analytics.setSingletonInstance(analytics)
    }

    private fun initShipBook() {
        ShipBook.start(this, BuildConfig.SHIPBOOK_APP_ID, BuildConfig.SHIPBOOK_APP_KEY)
        ShipBook.addWrapperClass(MTLogger::class.java.name)
    }

}
