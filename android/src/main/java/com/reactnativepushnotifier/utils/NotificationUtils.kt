package com.reactnativepushnotifier.utils

import android.Manifest
import android.app.*
import android.app.PendingIntent.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.permissions.PermissionsModule
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

  private fun callAlertDialog(context: Context, builder: AlertDialog.Builder) {
    val costumeSound = ResourcesResolver(context).getRaw("call_ringtone")
    val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://" + context.packageName + "/" + costumeSound)
    val ringtone = RingtoneManager.getRingtone(context, soundUri)
    ringtone?.play()
    val alertBuilder =
      builder.setCancelable(true)
      builder.setTitle("Incoming Call")
      builder.setMessage("Call from xxxx")
      builder.setPositiveButton(
        "Answer"
      ) { dialog, which ->
        ringtone.stop()
      }
      builder.setNegativeButton(
        "Reject"
      ) { dialog, which ->
        ringtone.stop()
      }
      val alert = alertBuilder.create()
      alert.show()
      val timer = Timer()
     timer.schedule(object : TimerTask() {
      override fun run() {
        alert.dismiss()
        timer.cancel()
        ringtone.stop()
      }
    }, 10000)
  }
  fun showActionNotification(context: Context, notificationData: ReadableMap, activity: Activity, promise: Promise) {
    val style = ResourcesResolver(context).getStyle("callDialog")
    val builder = AlertDialog.Builder(activity, android.R.style.Theme_Material_Light_Dialog_Alert)
    val notificationDataBundle = Arguments.toBundle(notificationData)
    val callerName = notificationDataBundle!!.getString("callerName", "")
    val costumeSound = ResourcesResolver(context).getRaw("call_ringtone")
    val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://" + context.packageName + "/" + costumeSound)
    val ringtone = RingtoneManager.getRingtone(context, soundUri)
    ringtone?.play()
    val alertBuilder =
      builder.setCancelable(true)
    builder.setTitle("Incoming Call")
    builder.setMessage("Call from $callerName")
    builder.setPositiveButton(
      "Answer"
    ) { dialog, which ->
      ringtone.stop()
      promise.resolve("answered")
    }
    builder.setNegativeButton(
      "Reject"
    ) { dialog, which ->
      ringtone.stop()
      promise.resolve("rejected")
    }
    val alert = alertBuilder.create()
    alert.show()
    val timer = Timer()
    timer.schedule(object : TimerTask() {
      override fun run() {
        alert.dismiss()
        timer.cancel()
        ringtone.stop()
        promise.resolve("expired")
      }
    }, 10000)
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
//    val fullScreenIntent = Intent(context, Class.forName(context.packageName).javaClass)
//
//    val fullScreenPendingIntent = getActivity(context, 0,
//      fullScreenIntent, FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)

    val notificationManager = NotificationManagerCompat.from(context)
    createCallChannel(notificationManager)
    val notificationId = UUID.randomUUID().hashCode()
    val channelId = INCOMING_CALL_CHANNEL_ID;
    val notificationIcon: Int = ResourcesResolver(context).getDrawable("ic_notify")
    val notificationDataBundle = Arguments.toBundle(notificationData)
    val title = notificationDataBundle!!.getString("title", "")
    val body = notificationDataBundle.getString("body", "")

    val pendingIntent = createNotificationIntent(context, notificationId, notificationDataBundle,"clicked")

//    val remoteView = RemoteViews(context.packageName, R.layout.notification_custom)

// Set the PendingIntent that will “shoot” when the button is clicked. A normal onClickListener won’t work here – again, the notification will live outside our process

//    remoteView.setOnClickPendingIntent(R.id.button_accept_call, pendingIntent)

// Add to our long-suffering builder

    val costumeSound = ResourcesResolver(context).getRaw("call_ringtone")
    val ringtone = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://" + context.packageName + "/" + costumeSound)

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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      notificationBuilder.priority = NotificationManager.IMPORTANCE_HIGH
    }
    notificationManager.notify(notificationId, notificationBuilder.build())
  }
}
