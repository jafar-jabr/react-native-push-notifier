package com.reactnativepushnotifier.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import java.util.*


object NotificationUtils {

    private const val DEFAULT_CHANNEL_ID="android_default_channel_id"
    private const val DEFAULT_CHANNEL_NAME="android_defaultChannelName"
    const val EXTRA_NOTIFICATION = "com.rn.simple.notifier.clicked"
    private const val INCOMING_CALL_CHANNEL_ID = "incoming_call_channel_id"
    private const val INCOMING_CALL_CHANNEL_NAME = "incoming_call_channel_Name"

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
  private fun createCallChannel(notificationManager: NotificationManagerCompat) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannelCompat.Builder(
        INCOMING_CALL_CHANNEL_ID,
        NotificationManagerCompat.IMPORTANCE_HIGH
      )
        .setName("Incoming calls")
        .setDescription("Incoming audio call alerts")
        .build()
      notificationManager.createNotificationChannel(channel)
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

  @JvmStatic
  fun showCallNotification(context: Context, notificationData: ReadableMap) {
    val fullScreenIntent = Intent(context, Class.forName(context.packageName))

    val fullScreenPendingIntent = getActivity(context, 0,
      fullScreenIntent, FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)

    val notificationManager = NotificationManagerCompat.from(context)
    createCallChannel(notificationManager)
    val notificationId = UUID.randomUUID().hashCode()
    val channelId = INCOMING_CALL_CHANNEL_ID;
    val notificationIcon: Int = ResourcesResolver(context).getDrawable("ic_notify")
    val notificationDataBundle = Arguments.toBundle(notificationData)
    val title = notificationDataBundle!!.getString("title", "")
    val body = notificationDataBundle.getString("body", "")

    val costumeSound = ResourcesResolver(context).getRaw("call_ringtone")
    val ringtone = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://" + context.packageName + "/" + costumeSound)
    val pendingIntent = createNotificationIntent(context, notificationId, notificationDataBundle,"clicked")
    val notificationBuilder: NotificationCompat.Builder =
      NotificationCompat.Builder(context, channelId)
        .setSmallIcon(notificationIcon)
        .setContentTitle(title)
        .setContentText(body)
        .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
        .setColor(Color.CYAN)
        .setCategory(NotificationCompat.CATEGORY_CALL)
        .setSound(ringtone)
        .setVibrate(longArrayOf(1L, 2L, 3L))
        .setFullScreenIntent(fullScreenPendingIntent, true)
    notificationManager.notify(notificationId, notificationBuilder.build())
  }
}
