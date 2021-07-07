package com.bupp.wood_spoon_eaters.common

import android.content.SharedPreferences
import android.util.Log
import com.bupp.wood_spoon_eaters.FlavorConfig

class FlavorConfigManager(private val sharedPreferences: SharedPreferences) {


    companion object{
        const val DEFAULT_STAGING_ENVIROMENT = FlavorConfig.BASE_URL
        const val TAG = "wowFlavorConfigManager"
        const val SYSTEM_ENVIRONMENT = "system_environment"
    }

    val CURRENT_USR_COUNTRY = "cur_usr_country"

    var curEnvironment: String
        get() = sharedPreferences.getString(SYSTEM_ENVIRONMENT, "")!!
        set(curEnvironment){
            sharedPreferences.edit().putString(SYSTEM_ENVIRONMENT, curEnvironment).commit()
        }


    fun setEnvironment(env: String) {
        Log.d(TAG, "environment end point branch: $env")
        this.curEnvironment = env
//        RetrofitUrlManager.getInstance().setGlobalDomain(getBaseUrl())
    }

    fun getBaseUrl(): String {
        val baseUrl = if(curEnvironment.isNotEmpty()){
            "https://woodspoon-server-pr-$curEnvironment.herokuapp.com/api/v1/"
        }else{
            FlavorConfig.BASE_URL
        }
        Log.d(TAG, "getBaseUrl: $baseUrl")
        return baseUrl
    }

    fun getEnvName(): String{
        if (curEnvironment.isNotEmpty()){
            return "(pr-$curEnvironment)"
        }
        return ""
    }

}