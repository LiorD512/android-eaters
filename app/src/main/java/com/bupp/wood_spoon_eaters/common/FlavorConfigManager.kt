package com.bupp.wood_spoon_eaters.common

import android.content.SharedPreferences
import android.util.Log
import com.bupp.wood_spoon_eaters.FlavorConfig

class FlavorConfigManager(private val sharedPreferences: SharedPreferences) {


    companion object{
        const val TAG = "wowFlavorConfigManager"
        const val SYSTEM_ENVIRONMENT = "system_environment"
        const val CUSTOM_BASE_URL = "custom_base_url"
    }

    var curEnvironment: String?
        get() = sharedPreferences.getString(SYSTEM_ENVIRONMENT, null)
        set(curEnvironment){
            sharedPreferences.edit().putString(SYSTEM_ENVIRONMENT, curEnvironment).commit()
        }

    var curBaseUrl: String?
        get() = sharedPreferences.getString(CUSTOM_BASE_URL, "")
        set(curEnvironment){
            sharedPreferences.edit().putString(CUSTOM_BASE_URL, curEnvironment).commit()
        }


    fun setEnvironment(env: String) {
        Log.d(TAG, "environment end point branch: $env")
        this.curEnvironment = env
    }

    fun setCustomBaseUrl(baseUrl: String) {
        Log.d(TAG, "setBaseUrl: $baseUrl")
        this.curBaseUrl = baseUrl
    }

    fun getBaseUrl(): String {
        Log.d(TAG, "getBaseUrl env: $curEnvironment")
        var finalUrl: String
        if(curBaseUrl?.isNotEmpty() == true){
            finalUrl = curBaseUrl!!
        }else{
             if(curEnvironment?.isNotEmpty() == true){
                 Log.d(TAG, "curEnvironment: $curEnvironment")
                finalUrl = "https://woodspoon-server-pr-$curEnvironment.herokuapp.com/api/v2/"
            }else{
                 finalUrl = FlavorConfig.BASE_URL
            }
            Log.d(TAG, "getBaseUrl: $finalUrl")
        }
        return finalUrl
    }

    fun getEnvName(): String{
        if (curEnvironment?.isNotEmpty() == true){
            return "(pr-$curEnvironment)"
        }
        return ""
    }

}