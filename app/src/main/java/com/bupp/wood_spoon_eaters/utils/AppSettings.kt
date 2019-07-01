package com.bupp.wood_spoon_eaters.utils

import android.content.SharedPreferences
import com.bupp.wood_spoon_eaters.model.Client

class AppSettings(val sharedPreferences: SharedPreferences) {

    val HAS_FINISH_STORY = "has_finished_story"

    var currentClient: Client? = null

    //singup params
//    var hasAccount: Boolean
//    get() = sharedPreferences.getBoolean(HAS_ACCOUNT, false)
//    set(hasAccount) = sharedPreferences.edit().putBoolean(HAS_ACCOUNT, hasAccount).apply()

    var hasFinishedStory: Boolean
        get() = sharedPreferences.getBoolean(HAS_FINISH_STORY, false)
        set(hasFinishedStory) = sharedPreferences.edit().putBoolean(HAS_FINISH_STORY, hasFinishedStory).apply()


    fun isAfterLogin(): Boolean {
        return true
    }

    //shared prefs
//    fun getToken(): String? {
//        return sharedPreferences.getString(Constants.PREFS_KEY_TOKEN, null)
//    }
//
//    fun setToken(token: String) {
//        sharedPreferences.edit().putString(Constants.PREFS_KEY_TOKEN, token).apply()
//    }

    fun isFirstTime(): Boolean {
        return sharedPreferences.getBoolean(Constants.PREFS_KEY_IS_FIRST_TIME, true)
    }

    fun setFirstTime(isFirstTime: Boolean) {
        sharedPreferences.edit().putBoolean(Constants.PREFS_KEY_IS_FIRST_TIME, isFirstTime).apply()
    }

    fun shouldShowNotAvailableDialog(): Boolean {
        return sharedPreferences.getBoolean(Constants.SHOW_NOT_AVAILABLE_DIALOG, true)
    }

    fun setShouldShowNotAvailableDialog(shouldShow: Boolean) {
        sharedPreferences.edit().putBoolean(Constants.SHOW_NOT_AVAILABLE_DIALOG, shouldShow).apply()
    }

    fun setUseLocation(isUsingLocation: Boolean) {
        sharedPreferences.edit().putBoolean(Constants.USE_LOCATION, isUsingLocation).apply()
    }

    fun setAlerts(alert: Boolean) {
        sharedPreferences.edit().putBoolean(Constants.SEND_ALERTS, alert).apply()
    }

    fun setEmails(sendEmail: Boolean) {
        sharedPreferences.edit().putBoolean(Constants.SEND_EMAILS, sendEmail).apply()
    }

    fun getUseLocation(): Boolean {
        return sharedPreferences.getBoolean(Constants.USE_LOCATION, false)
    }

    fun getAlerts(): Boolean {
        return sharedPreferences.getBoolean(Constants.SEND_ALERTS, false)
    }

    fun getEmails(): Boolean {
        return sharedPreferences.getBoolean(Constants.SEND_EMAILS, false)
    }
}
