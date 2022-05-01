package com.bupp.wood_spoon_chef.fcm
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bupp.wood_spoon_chef.R

private const val NOTIFICATION_CHANNEL_ID = "86969698"
private const val NOTIFICATION_CHANNEL_NAME = "WoodSpoonChef"
private const val GROUP_KEY_HEY_EXPERTS = "com.bupp.wood_spoon_chef"

class NotificationsHelper {

    companion object {

        private fun createNotificationChannelIfNeeded(
            notificationManager: NotificationManager
        ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(
                    NOTIFICATION_CHANNEL_ID
                ) == null
            ) {
                val notificationChannel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )

                notificationChannel.setShowBadge(true)
                notificationChannel.enableLights(true)
                notificationChannel.enableVibration(true)
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }

        fun sendNotification(
            context: Context,
            title: String,
            message: String,
            intent: PendingIntent
        ) {

            val notificationManager: NotificationManager?

            try {
                notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                createNotificationChannelIfNeeded(
                    notificationManager
                )

                val defaultSoundUri =
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val bm = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_round)

                val appName = context.getString(R.string.app_name)

                val inboxStyle = NotificationCompat.InboxStyle()
                inboxStyle.setBigContentTitle(appName)
                inboxStyle.addLine(message)

                val notificationBuilder = NotificationCompat.Builder(
                    context,
                    NOTIFICATION_CHANNEL_ID
                )
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setLargeIcon(bm)
                    .setGroupSummary(true)
                    .setGroup(GROUP_KEY_HEY_EXPERTS)
                    .setStyle(inboxStyle)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(intent)

                notificationManager.notify(
                    0,
                    notificationBuilder.build()
                )
            } catch (ex: Exception) {
                //do nothing
            }
        }
    }
}