package com.bupp.wood_spoon_eaters.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.bupp.wood_spoon_eaters.features.splash.SplashActivity
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

    fun logout(context: Context){
        sharedPreferences.edit().clear().commit()
        val intent = Intent(context, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
    }


}
