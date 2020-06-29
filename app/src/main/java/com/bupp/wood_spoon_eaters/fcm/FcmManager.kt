package com.bupp.wood_spoon_eaters.fcm

import android.os.Build
import android.util.Log
import com.bupp.wood_spoon_eaters.BuildConfig
import com.bupp.wood_spoon_eaters.fcm.MyFirebaseMessagingService
import com.bupp.wood_spoon_eaters.model.Device
import com.bupp.wood_spoon_eaters.model.DeviceDetails
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.google.firebase.iid.FirebaseInstanceId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FcmManager(val api: ApiService) : MyFirebaseMessagingService.FirebaseMessagingServiceListeners {

    fun getDeviceType(): String {
        return Build.DEVICE + " - " + Build.MODEL
    }

    fun getOsType(): Int {
        return 1
    }

    fun getOsVersion(): String {
        return Build.VERSION.SDK_INT.toString()
    }

    fun getAppVersion(): String {
        return BuildConfig.VERSION_NAME.toString()
    }

    fun refreshPushNotificationToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            Log.d("wowFCMManager", "refreshed token: " + instanceIdResult.token)
            onTokenRefreshed(instanceIdResult.token)
//            val device = getDeviceDetails(instanceIdResult.token)
//            api.postDeviceDetails(device).enqueue(object : Callback<ServerResponse<Void>> {
//                override fun onResponse(call: Call<ServerResponse<Void>>, response: Response<ServerResponse<Void>>) {
//                    if (response.isSuccessful) {
//                        Log.d("wowFCMManager", "refreshPushNotificationToken success")
//                    } else {
//                        Log.d("wowFCMManager", "refreshPushNotificationToken fail")
//                    }
//                }
//
//                override fun onFailure(call: Call<ServerResponse<Void>>, t: Throwable) {
//                    Log.d("wowFCMManager", "refreshPushNotificationToken big fail")
//                }
//            })
        }
    }

    fun initFcmListener(){
        MyFirebaseMessagingService().setFCMListener(this)
    }

    fun getDeviceDetails(token: String): DeviceDetails {
        val deviceDetails = DeviceDetails(device = Device(deviceToken = token, deviceType = getDeviceType(), osType = getOsType(), osVersion = getOsVersion(), appVersion = getAppVersion()))
        return deviceDetails
    }

    override fun onTokenRefreshed(token: String) {
        Log.d("wowFCMManager", "onTokenRefreshed - token: " + token)
        val device = getDeviceDetails(token)
        api.postDeviceDetails(device).enqueue(object : Callback<ServerResponse<Void>> {
            override fun onResponse(call: Call<ServerResponse<Void>>, response: Response<ServerResponse<Void>>) {
                if (response.isSuccessful) {
                    Log.d("wowFCMManager", "onTokenRefreshed success")
                } else {
                    Log.d("wowFCMManager", "onTokenRefreshed fail")
                }
            }

            override fun onFailure(call: Call<ServerResponse<Void>>, t: Throwable) {
                Log.d("wowFCMManager", "onTokenRefreshed big fail")
            }
        })
    }
}