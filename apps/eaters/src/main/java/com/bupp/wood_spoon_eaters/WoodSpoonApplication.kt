package com.bupp.wood_spoon_eaters

import android.app.Application
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.di.appModule
import com.bupp.wood_spoon_eaters.di.networkModule
import com.bupp.wood_spoon_eaters.di.rateApp
import com.bupp.wood_spoon_eaters.repositories.AppSettingsRepository
import com.bupp.wood_spoon_eaters.repositories.EatersFeatureFlags
import com.bupp.wood_spoon_eaters.repositories.cachedFeatureFlag
import com.eatwoodspoon.analytics.DeviceId
import com.eatwoodspoon.analytics.SessionId
import com.eatwoodspoon.analytics.analyticsModule
import com.eatwoodspoon.analytics.app_attributes.AppAttributesDataSource
import com.eatwoodspoon.analytics.app_attributes.AppAttributesDataSourceFactory
import com.eatwoodspoon.auth.authModule
import com.eatwoodspoon.logsender.Logger
import com.eatwoodspoon.logsender.LoggerConfig
import com.eatwoodspoon.logsender.Preprocessor
import com.eatwoodspoon.logsender.S3SenderConfig
import com.eatwoodspoon.timber.S3SenderTree
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
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
import timber.log.Timber


class WoodSpoonApplication : Application() {

    companion object {
        private lateinit var instance: WoodSpoonApplication
        fun getInstance(): WoodSpoonApplication = instance
    }

    override fun onCreate() {
        super.onCreate()
        initLogging()

        Timber.i("test_event_app_started")

        // start Koin context
        startKoin {
            allowOverride(true)
            androidLogger()
            androidContext(this@WoodSpoonApplication)
            modules(networkModule + analyticsModule + rateApp + authModule + appModule)
        }

        FacebookSdk.setIsDebugEnabled(true)
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS)

        AppCenter.start(
            this, getString(R.string.app_center_key),
            com.microsoft.appcenter.analytics.Analytics::class.java, Crashes::class.java, Distribute::class.java
        )

        Branch.enableLogging()
        Branch.getAutoInstance(this)
        Branch.enableTestMode()

        if (BuildConfig.DEBUG) {
            UXCam.optOutOverall()
        } else {
            UXCam.startWithKey(getString(R.string.ux_cam_app_key))
        }

        val analytics =
            Analytics.Builder(
                applicationContext,
                getString(R.string.segment_jey)
            ) // Enable this to record certain application events automatically!
                .trackApplicationLifecycleEvents() // Enable this to record screen views automatically!
//                .logLevel(Analytics.LogLevel.VERBOSE)
                .use(MixpanelIntegration.FACTORY)
                .use(AppsflyerIntegration.FACTORY)
                .build()
        Analytics.setSingletonInstance(analytics)

        MTLogger.Builder()
            .showLinks(true)
            .autoTag(true)
            .tagPrefix("MTL")
            .build()
    }

    private fun initShipBook() {
        if(AppSettingsRepository.cachedFeatureFlag(EatersFeatureFlags.LogShipbookEnabled, this) != false) {
            ShipBook.start(this, BuildConfig.SHIPBOOK_APP_ID, BuildConfig.SHIPBOOK_APP_KEY)
            ShipBook.addWrapperClass(MTLogger::class.java.name)
        }
    }

    private fun initLogging() {
        initShipBook()
        if(AppSettingsRepository.cachedFeatureFlag(EatersFeatureFlags.LogS3Enabled, this) != false) {
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