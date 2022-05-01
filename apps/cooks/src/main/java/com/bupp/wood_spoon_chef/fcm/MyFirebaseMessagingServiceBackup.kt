package com.bupp.wood_spoon_chef.fcm//package com.bupp.wood_spoon_chef.fcm
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.media.RingtoneManager
//import android.os.Build
//import android.util.Log
//import androidx.core.app.NotificationCompat
//import com.bupp.wood_spoon_chef.R
//import com.bupp.wood_spoon_chef.presentation.features.main.MainActivity
//import com.google.android.gms.tasks.OnCompleteListener
//import com.google.firebase.iid.FirebaseInstanceId
//import com.google.firebase.messaging.FirebaseMessaging
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//import java.util.ArrayList
//
//class MyFirebaseMessagingServiceBackup : FirebaseMessagingService() {
//
//    interface FirebaseMessagingServiceListeners {
//        fun onTokenRefreshed(token: String)
//    }
//
//    var listeners: MutableList<FirebaseMessagingServiceListeners>? = null
//
//    fun setFCMListener(listener: FirebaseMessagingServiceListeners) {
//        if (listeners == null) {
//            listeners = ArrayList()
//        }
//        listeners!!.add(listener)
//    }
//
//    fun refreshToken(listener: FirebaseMessagingServiceListeners) {
//        setFCMListener(listener)
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//
//            // Get new FCM registration token
//            val token = task.result
//
//            Log.d("wowFireBaseTokenService", "refreshed token: " + token)
//            listener.onTokenRefreshed(token)
//        })
//    }
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//
//        // TODO(developer): Handle FCM messages here.
//        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d(TAG, "From: ${remoteMessage.from}")
//
//        // Check if message contains a data payload.
//        remoteMessage.data.isNotEmpty().let {
//            Log.d(TAG, "Message data payload: " + remoteMessage.data)
//            if(remoteMessage.data.containsValue("message")){
//                val msg = remoteMessage.data.getValue("message")
//                sendNotification(messageBody = msg)
//            }
////            handleNow()
//
//        }
//
//        // Check if message contains a notification payload.
//        remoteMessage.notification?.let {
//            Log.d(TAG, "Message Notification Body: ${it.body}")
//            sendNotification(messageTitle = it.title, messageBody = it.body)
//        }
//
//        // Also if you intend on generating your own notifications as a result of a received FCM
//        // message, here is where that should be initiated. See sendNotification method below.
//    }
//
//    override fun onNewToken(token: String) {
//        Log.d(TAG, "Refreshed token: $token")
//        if(listeners != null){
//            for(listener in listeners!!){
//                Log.d(TAG, "sendRegistrationTokenToServer($token)")
//                listener.onTokenRefreshed(token)
//            }
//        }
//    }
//
//    private fun handleNow() {
//        Log.d(TAG, "Short lived task is done.")
//    }
//
//
//    private fun sendNotification(messageTitle: String? = null, messageBody: String? = null) {
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//            PendingIntent.FLAG_ONE_SHOT)
//
//        val channelId = getString(R.string.default_notification_channel_id)
//        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.mipmap.ic_launcher_round)
//            .setContentTitle(messageTitle ?: getString(R.string.fcm_message_title))
//            .setContentText(messageBody)
//            .setAutoCancel(true)
//            .setSound(defaultSoundUri)
//            .setContentIntent(pendingIntent)
//            .setStyle(NotificationCompat.BigTextStyle()
//            .bigText(messageBody))
//
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(channelId,
//                "Channel human readable title",
//                NotificationManager.IMPORTANCE_DEFAULT)
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
//    }
//
//    companion object {
//        private const val TAG = "wowMyFirebaseMsgService"
//    }
//}