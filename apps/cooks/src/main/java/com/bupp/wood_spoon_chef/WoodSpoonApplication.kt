package com.bupp.wood_spoon_chef

import android.app.Application
import com.bupp.wood_spoon_chef.common.MTLogger
import com.bupp.wood_spoon_chef.data.repositories.AppSettingsRepository
import com.bupp.wood_spoon_chef.data.repositories.CooksFeatureFlags
import com.bupp.wood_spoon_chef.data.repositories.cachedFeatureFlag
import com.bupp.wood_spoon_chef.di.appModule
import com.bupp.wood_spoon_chef.di.networkModule
import com.bupp.wood_spoon_chef.di.cookingSlotModule
import com.eatwoodspoon.analytics.DeviceId
import com.eatwoodspoon.analytics.SessionId
import com.eatwoodspoon.analytics.analyticsModule
import com.eatwoodspoon.analytics.app_attributes.AppAttributesDataSource
import com.eatwoodspoon.analytics.app_attributes.AppAttributesDataSourceFactory
import com.eatwoodspoon.logsender.Logger
import com.eatwoodspoon.logsender.LoggerConfig
import com.eatwoodspoon.logsender.Preprocessor
import com.eatwoodspoon.logsender.S3SenderConfig
import com.eatwoodspoon.timber.S3SenderTree
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
import timber.log.Timber


class WoodSpoonApplication : Application() {

    companion object {
        private lateinit var instance: WoodSpoonApplication

        fun getInstance(): WoodSpoonApplication = instance
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        initLogging()
        Timber.i("test_event_app_started")


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
        if(AppSettingsRepository.cachedFeatureFlag(CooksFeatureFlags.mobile_log_shipbook_enabled, this) != false) {
            ShipBook.start(this, BuildConfig.SHIPBOOK_APP_ID, BuildConfig.SHIPBOOK_APP_KEY)
            ShipBook.addWrapperClass(MTLogger::class.java.name)
        }
    }

    private fun initLogging() {
        initShipBook()
        if(AppSettingsRepository.cachedFeatureFlag(CooksFeatureFlags.mobile_log_s3_enabled, this) != false) {
            Logger.createSingletonInstance(
                this,
                LoggerConfig(
                    s3config = S3SenderConfig(
                        credentials = S3SenderConfig.Credentials.fromAssets(
                            this,
                            "aws-logger-credentials.properties"
                        ),
                        deviceId = DeviceId.getValue(this),
                        logsDirectoryName = "Logs"
                    )
                ),
                object : Preprocessor {
                    val appAttributesDataSource: AppAttributesDataSource by lazy {
                        AppAttributesDataSourceFactory.create(this@WoodSpoonApplication)
                    }

                    override fun process(logItem: Map<String, Any>): Map<String, Any> {
                        return appAttributesDataSource.appAttributes + mapOf(
                            "session_id" to SessionId.value,
                            "timestamp" to System.currentTimeMillis(),
                            "device_id" to DeviceId.getValue(this@WoodSpoonApplication),
                        ) + logItem
                    }
                },
            )
            Timber.plant(S3SenderTree(Logger.instance))
        }

        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}
