package com.bupp.wood_spoon_eaters.fcm

import android.os.Build
import android.util.Log
import androidx.annotation.NonNull
import com.bupp.wood_spoon_eaters.BuildConfig
import com.bupp.wood_spoon_eaters.model.Device
import com.bupp.wood_spoon_eaters.model.DeviceDetails
import com.bupp.wood_spoon_eaters.model.ServerResponse
import com.bupp.wood_spoon_eaters.network.ApiService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FcmManager(val api: ApiService) : MyFirebaseMessagingService.FirebaseMessagingServiceListeners {

    private fun getDeviceType(): String {
        return Build.DEVICE + " - " + Build.MODEL
    }

    private fun getOsType(): Int {
        return 1
    }

    private fun getOsVersion(): String {
        return Build.VERSION.SDK_INT.toString()
    }

    private fun getAppVersion(): String {
        return BuildConfig.VERSION_NAME.toString()
    }

    fun refreshPushNotificationToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(object : OnCompleteListener<String?> {
                override fun onComplete(@NonNull task: Task<String?>) {
                    if (!task.isSuccessful) {
                        Log.w("wowFCMManager", "Fetching FCM registration token failed", task.exception)
                        return
                    }

                    // Get new FCM registration token
                    val token: String? = task.result
                    token?.let {
                        Log.d("wowFCMManager", "refreshed token: $token")
                        onTokenRefreshed(token)
                    }
                }
            })
    }

    fun initFcmListener() {
        MyFirebaseMessagingService().setFCMListener(this)
    }

    private fun getDeviceDetails(token: String): DeviceDetails {
        return DeviceDetails(
            device = Device(
                deviceToken = token,
                deviceType = getDeviceType(),
                osType = getOsType(),
                osVersion = getOsVersion(),
                appVersion = getAppVersion()
            )
        )
    }

    override fun onTokenRefreshed(token: String) {
        Log.d("wowFCMManager", "onTokenRefreshed - token: $token")
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