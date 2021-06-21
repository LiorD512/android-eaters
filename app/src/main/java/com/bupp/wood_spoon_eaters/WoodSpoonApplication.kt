package com.bupp.wood_spoon_eaters

import android.app.Application
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.bupp.wood_spoon_eaters.common.MTLogger
import com.bupp.wood_spoon_eaters.di.appModule
import com.bupp.wood_spoon_eaters.di.networkModule
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import com.facebook.appevents.AppEventsLogger
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import com.segment.analytics.Analytics
import com.segment.analytics.android.integrations.appsflyer.AppsflyerIntegration
import com.uxcam.UXCam
import io.branch.referral.Branch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import com.segment.analytics.android.integrations.mixpanel.MixpanelIntegration;


class WoodSpoonApplication : Application() {
    private val devKey  = "pdDuzVhDY8UpBHir8tgvKc"

    companion object {
        private lateinit var instance: WoodSpoonApplication
        fun getInstance(): WoodSpoonApplication = instance
    }

    override fun onCreate() {
        super.onCreate()

        // start Koin context
        startKoin {
            androidContext(this@WoodSpoonApplication)
            androidLogger()
            modules(listOf(appModule, networkModule))
        }

        FacebookSdk.setIsDebugEnabled(true)
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS)

        AppCenter.start(
            this, "1995d4eb-7e59-44b8-8832-6550bd7752ff",
            com.microsoft.appcenter.analytics.Analytics::class.java, Crashes::class.java, Distribute::class.java
        )

        // Branch logging for debugging
        Branch.enableLogging()

        // Branch object initialization
        Branch.getAutoInstance(this)


        val conversionDataListener  = object : AppsFlyerConversionListener{
            override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {

            }

            override fun onConversionDataFail(p0: String?) {

            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                data?.map {
//                    Log.d("wowApplication", "onAppOpen_attribute: ${it.key} = ${it.value}")
                }
            }

            override fun onAttributionFailure(error: String?) {
//                Log.e("wowApplication", "error onAttributionFailure :  $error")
            }
        }

        AppsFlyerLib.getInstance().init(devKey, conversionDataListener, applicationContext)
        AppsFlyerLib.getInstance().start(this)

        if(BuildConfig.BUILD_TYPE.equals("release", true)) {
            Log.d("wowApplication", "uxcam is on!")
            UXCam.startWithKey(getString(R.string.ux_cam_app_key))
            val analytics = Analytics.Builder(applicationContext, "ArTgdJ2yAsbjtEuQL4PYyeLDOHJ6k4xg") // Enable this to record certain application events automatically!
                .trackApplicationLifecycleEvents() // Enable this to record screen views automatically!
//                .recordScreenViews()
//                .logLevel(Analytics.LogLevel.VERBOSE)
                .use(MixpanelIntegration.FACTORY)
                .use(AppsflyerIntegration.FACTORY)
                .build()
            Analytics.setSingletonInstance(analytics)
        }else{
            val analytics = Analytics.Builder(applicationContext, "dBQhDMRWdKAvkBKC53ind9Pey34RuuQP") // Enable this to record certain application events automatically!
                .trackApplicationLifecycleEvents() // Enable this to record screen views automatically!
//                .recordScreenViews()
                .logLevel(Analytics.LogLevel.VERBOSE)
                .use(MixpanelIntegration.FACTORY)
                .use(AppsflyerIntegration.FACTORY)
                .build()

            Analytics.setSingletonInstance(analytics)
        }

        MTLogger.Builder()
            .showLinks(true)
            .autoTag(true)
            .tagPrefix("wow")
            .build()

    }

//        LeakSentry.config = LeakSentry.config.copy(watchFragmentViews = true)

}