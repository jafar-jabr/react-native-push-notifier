package com.reactnativepushnotifier.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import android.app.PendingIntent
import android.os.Bundle


object NotificationUtils {

    private const val DEFAULT_CHANNEL_ID="android_default_channel_id"
    private const val DEFAULT_CHANNEL_NAME="android_defaultChannelName"
    const val EXTRA_NOTIFICATION = "com.rn.simple.notifier.clicked"

    private fun createDefaultChannel(notificationManager: NotificationManager){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val defaultChannel = NotificationChannel(
                    DEFAULT_CHANNEL_ID,
                    DEFAULT_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(defaultChannel)
        }
    }

  private fun createNotificationIntent(
    context: Context,
    notificationId: Int,
    notificationDataBundle: Bundle,
    actionType: String
  ): PendingIntent? {
    val clickIntentData = Intent(context, NotificationsBroadcastReceiver::class.java)
    clickIntentData.putExtra("action", actionType)
    clickIntentData.putExtra("notificationId", notificationId)

    clickIntentData.putExtra(EXTRA_NOTIFICATION, notificationDataBundle)
    return getBroadcast(context, notificationId, clickIntentData, FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT)
  }
    @JvmStatic
    fun showInfoNotification(context: Context, notificationData: ReadableMap, notificationId: Int, soundFile: String) {
        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
      if(notificationManager != null) {
        createDefaultChannel(notificationManager)
      }
        val channelId = DEFAULT_CHANNEL_ID;
        val notificationIcon: Int = ResourcesResolver(context).getDrawable("notification_icon")
        val notificationDataBundle = Arguments.toBundle(notificationData)
        val title = notificationDataBundle!!.getString("title", "")
        val body = notificationDataBundle.getString("body", "")

        val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(notificationIcon)
                        .setContentIntent(createNotificationIntent(context, notificationId, notificationDataBundle,"clicked"))
                        .setContentTitle(title)
                        .setContentText(body)
                        .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                        .setColor(Color.CYAN)
                if(soundFile == "default") {
                  notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE)
                    .setDefaults(Notification.DEFAULT_SOUND)
                } else {
                  notificationBuilder.setDeleteIntent(createNotificationIntent(context, notificationId, notificationDataBundle,"deleted"))
                }

        notificationManager?.notify(notificationId, notificationBuilder.build())
    }
}
