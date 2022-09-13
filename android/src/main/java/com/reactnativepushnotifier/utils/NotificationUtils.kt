package com.reactnativepushnotifier.utils

import android.app.*
import android.app.NotificationManager.IMPORTANCE_MAX
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.reactnativepushnotifier.R
import java.util.*


object NotificationUtils {

    private const val DEFAULT_CHANNEL_ID="android_default_channel_id"
    private const val DEFAULT_CHANNEL_NAME="android_defaultChannelName"
    const val EXTRA_NOTIFICATION = "com.rn.simple.notifier.clicked"
    private const val INCOMING_CALL_CHANNEL_ID = "incoming_call_channel_id"
    const val PUSH_NOTIFICATION_ID = 475683469

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
    notificationDataBundle: Bundle,
    actionType: String,
    notificationId: Int,
    activityName: String,
  ): PendingIntent? {
    val clickIntentData = Intent(context, NotificationsBroadcastReceiver::class.java)
    clickIntentData.putExtra("action", actionType)
    clickIntentData.putExtra("notificationId", notificationId)
    clickIntentData.putExtra("activityName", activityName)

    clickIntentData.putExtra(EXTRA_NOTIFICATION, notificationDataBundle)
    val requestCode = UUID.randomUUID().hashCode()

    return getBroadcast(context, requestCode, clickIntentData, FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT)
  }

  @RequiresApi(Build.VERSION_CODES.S)
  fun showCallNotification(context: Context, notificationData: ReadableMap, activity: Activity) {
    val notificationManager = NotificationManagerCompat.from(context)
    val notificationDataBundle = Arguments.toBundle(notificationData)
    val notificationId = PUSH_NOTIFICATION_ID

  val answerIntent = notificationDataBundle?.let {
      createNotificationIntent(context,
        it,"answer", notificationId, activity.componentName.className)
    }
    val rejectIntent = notificationDataBundle?.let {
      createNotificationIntent(context,
        it,"reject", notificationId, activity.componentName.className)
    }

    createCallChannel(notificationManager)
    val channelId = INCOMING_CALL_CHANNEL_ID;
    val callerName = notificationDataBundle?.getString("callerName", "")

    val remoteView = RemoteViews(context.packageName,R.layout.notification_custom)
    remoteView.setOnClickPendingIntent(R.id.imgAnswer, answerIntent)
    remoteView.setOnClickPendingIntent(R.id.imgDecline, rejectIntent)
    remoteView.setTextViewText(R.id.callerName, callerName);

    val notificationBuilder: NotificationCompat.Builder =
      NotificationCompat.Builder(context, channelId)
        .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
       // .setVibrate(longArrayOf(1L, 2L, 3L))
       // .setSmallIcon(R.drawable.logo_round)
       // .setContentTitle("Full Screen Alarm Test")
       // .setContentText("This is a test")
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setCategory(NotificationCompat.CATEGORY_CALL)
        .setFullScreenIntent(answerIntent, true)
         .setCustomContentView(remoteView).
          setCustomBigContentView(remoteView)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      notificationBuilder.priority = NotificationManager.IMPORTANCE_HIGH
    }
    notificationBuilder.build().flags = Notification.FLAG_NO_CLEAR or NotificationCompat.PRIORITY_MAX

    notificationManager.notify(notificationId, notificationBuilder.build())

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
                        .setContentIntent(createNotificationIntent(context, notificationDataBundle,"clicked", notificationId, ""))
                        .setContentTitle(title)
                        .setContentText(body)
                        .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                        .setColor(Color.CYAN)
                if(soundFile == "default") {
                  notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE)
                    .setDefaults(Notification.DEFAULT_SOUND)
                } else {
                  notificationBuilder.setDeleteIntent(createNotificationIntent(context, notificationDataBundle,"deleted", notificationId, ""))
                }

        notificationManager?.notify(notificationId, notificationBuilder.build())
    }
}
