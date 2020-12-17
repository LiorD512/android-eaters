package com.bupp.wood_spoon_eaters

import android.app.Application
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.bupp.wood_spoon_eaters.di.appModule
import com.bupp.wood_spoon_eaters.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.segment.analytics.Analytics
import com.uxcam.UXCam
import io.branch.referral.Branch


class WoodSpoonApplication : Application() {
    private val devKey  = "pdDuzVhDY8UpBHir8tgvKc"

    override fun onCreate() {
        super.onCreate()

        // start Koin context
        startKoin {
            androidContext(this@WoodSpoonApplication)
            androidLogger()
            modules(listOf(appModule, networkModule))
        }

        // Branch logging for debugging
        Branch.enableLogging()

        // Branch object initialization
        Branch.getAutoInstance(this)


        val conversionDataListener  = object : AppsFlyerConversionListener{
            override fun onInstallConversionDataLoaded(data: MutableMap<String, String>?) {
                data?.let { cvData ->
                    cvData.map {
//                        Log.i("wowApplication", "conversion_attribute:  ${it.key} = ${it.value}")
                    }
                }
            }

            override fun onInstallConversionFailure(error: String?) {
//                Log.e("wowApplication", "error onAttributionFailure :  $error")
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
        AppsFlyerLib.getInstance().startTracking(this)

        if(BuildConfig.BUILD_TYPE.equals("release", true)) {
            Log.d("wowApplication","uxcam is on!")
            UXCam.startWithKey(getString(R.string.ux_cam_app_key))

            val analytics: Analytics = Analytics.Builder(this@WoodSpoonApplication, "dBQhDMRWdKAvkBKC53ind9Pey34RuuQP") // Enable this to record certain application events automatically!
                .trackApplicationLifecycleEvents() // Enable this to record screen views automatically!
                .recordScreenViews()
                .build()

            Analytics.setSingletonInstance(analytics)
        }

    }

//        LeakSentry.config = LeakSentry.config.copy(watchFragmentViews = true)

}