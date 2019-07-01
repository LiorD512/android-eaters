package com.bupp.wood_spoon_eaters.utils

import android.content.SharedPreferences
import com.bupp.wood_spoon_eaters.model.Client
import com.bupp.wood_spoon_eaters.model.Eater

class AppSettings(val sharedPreferences: SharedPreferences) {

    val HAS_FINISH_STORY = "has_finished_story"

    var currentEater: Eater? = null

    //singup params
//    var hasAccount: Boolean
//    get() = sharedPreferences.getBoolean(HAS_ACCOUNT, false)
//    set(hasAccount) = sharedPreferences.edit().putBoolean(HAS_ACCOUNT, hasAccount).apply()

    fun isAfterLogin(): Boolean {
        return !currentEater?.email.isNullOrEmpty()
    }

//    fun setCurrentEater(client: Client?){
//        this.currentEater = client
//    }
//
//    fun getCurrentEater() : Client?{
//        return currentEater
//    }

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

}
