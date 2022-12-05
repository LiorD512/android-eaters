package com.bupp.wood_spoon_chef.fcm

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.util.Log
import com.bupp.wood_spoon_chef.presentation.features.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {

    interface FirebaseMessagingServiceListeners {
        fun onTokenRefreshed(token: String)
    }

    private var listeners: MutableList<FirebaseMessagingServiceListeners>? = null

    fun setFCMListener(listener: FirebaseMessagingServiceListeners) {
        if (listeners == null) {
            listeners = ArrayList()
        }
        listeners?.add(listener)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            try {
                val msg = remoteMessage.notification
                msg?.let{
                    sendNotification(it.title?:"", it.body?:"")
                }
            } catch (ex: Exception) {
                Log.d(TAG, "notification failed")
            }

        }
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        listeners?.let {
            for (listener in it) {
                listener.onTokenRefreshed(token)
            }
        }
    }

    private fun sendNotification(title: String ,message: String) {
        val resultIntent = Intent(this, MainActivity::class.java)
        val resultPendingIntent: PendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        NotificationsHelper.sendNotification(this, title ,message, resultPendingIntent)
    }

    companion object {
        private const val TAG = "wowMyFirebaseMsgService"
    }
}