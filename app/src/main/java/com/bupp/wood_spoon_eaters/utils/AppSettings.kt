package com.bupp.wood_spoon_eaters.utils

import android.content.SharedPreferences
import com.bupp.wood_spoon_eaters.model.Eater

class AppSettings(val sharedPreferences: SharedPreferences) {



    fun isFirstTime(): Boolean {
        return sharedPreferences.getBoolean(Constants.PREFS_KEY_IS_FIRST_TIME, true)
    }

    fun setFirstTime(isFirstTime: Boolean) {
        sharedPreferences.edit().putBoolean(Constants.PREFS_KEY_IS_FIRST_TIME, isFirstTime).apply()
    }


    fun hasFavoriets(): Boolean {
        //todo: change after gils refactoring eater model
//        return currentEater.favoritesCount > 0
        return false
    }

    var shouldEnabledUserLocation: Boolean
        get() = sharedPreferences.getBoolean(Constants.ENABLE_USER_LOCATION, true)
        set(isEnabled) = sharedPreferences.edit().putBoolean(Constants.ENABLE_USER_LOCATION, isEnabled).apply()

    var shouldEnabledOrderStatusAlerts: Boolean
        get() = sharedPreferences.getBoolean(Constants.ENABLE_STATUS_ALERTS, true)
        set(isEnabled) = sharedPreferences.edit().putBoolean(Constants.ENABLE_STATUS_ALERTS, isEnabled).apply()

    var shouldEnabledCommercialEmails: Boolean
        get() = sharedPreferences.getBoolean(Constants.ENABLE_COMMERCIAL_EMAILS, true)
        set(isEnabled) = sharedPreferences.edit().putBoolean(Constants.ENABLE_COMMERCIAL_EMAILS, isEnabled).apply()




}
