package com.bupp.wood_spoon_chef.fcm


import android.os.Build
import android.util.Log
import com.bupp.wood_spoon_chef.BuildConfig
import com.bupp.wood_spoon_chef.data.remote.model.Device
import com.bupp.wood_spoon_chef.data.remote.model.DeviceDetails
import com.bupp.wood_spoon_chef.data.remote.network.ApiService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FcmManager(private val apiService: ApiService) :
        MyFirebaseMessagingService.FirebaseMessagingServiceListeners {

    init {
        setFCMListener()
    }

    private fun setFCMListener() {
        MyFirebaseMessagingService().setFCMListener(this)
    }

    fun refreshPushNotificationToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            onTokenRefreshed(token)
        })
    }

    override fun onTokenRefreshed(token: String) {
        GlobalScope.launch {
            launch(Dispatchers.IO) {
                try {
                    handleFcmToken(token)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Log.d(TAG,"onTokenRefreshed failed")
                }
            }
        }
    }

    private suspend fun handleFcmToken(token: String) = withContext(Dispatchers.IO) {
        try {
            val deviceInfo = getDeviceDetails(token)
            Log.d(TAG,"deviceInfo: $deviceInfo")
            apiService.postDeviceDetails(deviceInfo)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    private fun getDeviceDetails(token: String): DeviceDetails {
        return DeviceDetails(
            Device(
                deviceToken = token,
                deviceType = getDeviceType(),
                osType = getOsType(),
                osVersion = getOsVersion(),
                appVersion = getAppVersion()
        )
        )
    }

    private fun getDeviceType(): String {
        return Build.DEVICE + " - " + Build.MODEL
    }

    private fun getOsType(): Int {
        return OS_TYPE_ANDROID
    }

    private fun getOsVersion(): String {
        return Build.VERSION.RELEASE
    }

    private fun getAppVersion(): String {
        return BuildConfig.VERSION_NAME
    }

    companion object{
        const val TAG = "wowFcmManager"
        private const val OS_TYPE_ANDROID = 1
    }
}