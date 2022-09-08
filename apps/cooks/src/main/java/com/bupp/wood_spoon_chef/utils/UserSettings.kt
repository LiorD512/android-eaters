package com.bupp.wood_spoon_chef.utils

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import com.bupp.wood_spoon_chef.BuildConfig
import com.bupp.wood_spoon_chef.common.Constants
import com.bupp.wood_spoon_chef.presentation.features.splash.SplashActivity
import com.bupp.wood_spoon_chef.analytics.ChefAnalyticsTracker
import com.bupp.wood_spoon_chef.data.remote.model.Cook
import com.bupp.wood_spoon_chef.utils.extensions.clearStack


class UserSettings(
    private val sharedPreferences: SharedPreferences,
    private val chefAnalyticsTracker: ChefAnalyticsTracker
) {

    private val defaultToken = BuildConfig.DEFAULT_TOKEN

    companion object {
        const val TOKEN_KEY = "token_key"
    }

    var token: String?
        get() = sharedPreferences.getString(TOKEN_KEY, "").takeIf { !it.isNullOrBlank() } ?: defaultToken
        set(token) = sharedPreferences.edit().putString(TOKEN_KEY, token).apply()

    fun isRegistered(): Boolean {
        return token != defaultToken
    }

    var currentCook: Cook? = null


    fun isFirstTime(): Boolean {
        return sharedPreferences.getBoolean(Constants.PREFS_KEY_IS_FIRST_TIME, true)
    }

    fun shouldShowNotAvailableDialog(): Boolean {
        return sharedPreferences.getBoolean(Constants.SHOW_NOT_AVAILABLE_DIALOG, true)
    }

    fun setShouldShowNotAvailableDialog(shouldShow: Boolean) {
        sharedPreferences.edit().putBoolean(Constants.SHOW_NOT_AVAILABLE_DIALOG, shouldShow).apply()
    }

    fun logout(activity: Activity) {
        sharedPreferences.edit().clear().apply()

        val intent = Intent(activity, SplashActivity::class.java)
        intent.clearStack()
        activity.startActivity(intent)
        chefAnalyticsTracker.logout()
    }


}
