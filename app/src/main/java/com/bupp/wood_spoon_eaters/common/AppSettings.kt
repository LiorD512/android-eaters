package com.bupp.wood_spoon_eaters.common

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.bupp.wood_spoon_eaters.common.Constants
import com.bupp.wood_spoon_eaters.features.splash.SplashActivity

class AppSettings(private val sharedPreferences: SharedPreferences) {

    var shouldEnabledUserLocation: Boolean
        get() = sharedPreferences.getBoolean(Constants.ENABLE_USER_LOCATION, true)
        set(isEnabled) = sharedPreferences.edit().putBoolean(Constants.ENABLE_USER_LOCATION, isEnabled).apply()

    var shouldEnabledOrderStatusAlerts: Boolean
        get() = sharedPreferences.getBoolean(Constants.ENABLE_STATUS_ALERTS, true)
        set(isEnabled) = sharedPreferences.edit().putBoolean(Constants.ENABLE_STATUS_ALERTS, isEnabled).apply()

    var shouldEnabledCommercialEmails: Boolean
        get() = sharedPreferences.getBoolean(Constants.ENABLE_COMMERCIAL_EMAILS, true)
        set(isEnabled) = sharedPreferences.edit().putBoolean(Constants.ENABLE_COMMERCIAL_EMAILS, isEnabled).apply()

    var isFirstPurchase: Boolean
        get() = sharedPreferences.getBoolean(Constants.IS_FIRST_PURCHASE, true)
        set(isFirstTime) = sharedPreferences.edit().putBoolean(Constants.IS_FIRST_PURCHASE, isFirstTime).apply()



}
