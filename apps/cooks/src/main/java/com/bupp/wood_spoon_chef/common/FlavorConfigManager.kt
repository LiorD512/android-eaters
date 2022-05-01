package com.bupp.wood_spoon_chef.common

import android.content.SharedPreferences
import android.util.Log
import com.bupp.wood_spoon_chef.BuildConfig

class FlavorConfigManager(private val sharedPreferences: SharedPreferences) {


    companion object {
        const val TAG = "wowFlavorConfigManager"
        const val SYSTEM_ENVIRONMENT = "system_environment"
        const val CUSTOM_BASE_URL = "custom_base_url"
        const val VERSION_PLACE_HOLDER = "VERSION"
    }

    private var curEnvironment: String?
        get() = sharedPreferences.getString(SYSTEM_ENVIRONMENT, null)
        set(curEnvironment){
            sharedPreferences.edit().putString(SYSTEM_ENVIRONMENT, curEnvironment).commit()
        }

    private var curBaseUrl: String?
        get() = sharedPreferences.getString(CUSTOM_BASE_URL, "")
        set(curEnvironment){
            sharedPreferences.edit().putString(CUSTOM_BASE_URL, curEnvironment).commit()
        }

    fun setEnvironment(env: String) {
        Log.d(TAG, "environment end point branch: $env")
        this.curEnvironment = env
    }

    fun getBaseUrl(): String {
        Log.d(TAG, "getBaseUrl env: $curEnvironment")
        val finalUrl: String
        if (curBaseUrl?.isNotEmpty() == true) {
            finalUrl = curBaseUrl!!
        } else {
            finalUrl = if (curEnvironment?.isNotEmpty() == true) {
                Log.d(TAG, "curEnvironment: $curEnvironment")
                "https://woodspoon-server-pr-$curEnvironment.herokuapp.com/api/$VERSION_PLACE_HOLDER/"
            } else {
                BuildConfig.BASE_URL
            }
            Log.d(TAG, "getBaseUrl: $finalUrl")
        }
        return finalUrl
    }

}