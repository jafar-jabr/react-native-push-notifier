package com.reactnativepushnotifier.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap

object NotificationUtils {

    private const val DEFAULT_CHANNEL_ID="android_default_channel_id"
    private const val DEFAULT_CHANNEL_NAME="android_defaultChannelName"
    private const val COSTUME_CHANNEL_ID="android_costume_channel_id"
    private const val COSTUME_CHANNEL_NAME="android_costumeChannelName"
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

    private fun createCostumeChannel(context: Context, notificationManager: NotificationManager, soundFile: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val costumeChannel = NotificationChannel(
                    COSTUME_CHANNEL_ID,
                    COSTUME_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            )
            costumeChannel.lightColor = Color.GRAY
            costumeChannel.enableLights(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                costumeChannel.canBubble()
            }
            val soundResource = ResourcesResolver(context).getRaw(soundFile)
            val alarmSound: Uri = Uri.parse("android.resource://" + context.packageName + "/" + soundResource)
            val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
            costumeChannel.setSound(alarmSound, audioAttributes)
            notificationManager.createNotificationChannel(costumeChannel)
        }
    }
    private fun getSound(context: Context, soundFile: String):Uri{
        return if (soundFile == "default") {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }else {
            val nn = ResourcesResolver(context).getRaw(soundFile)
            Uri.parse("android.resource://" + context.packageName + "/" + nn)
        }
    }

    fun showActionNotification(context: Context, notificationData: ReadableMap, soundFile: String) {
        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        if (soundFile == "default" && notificationManager != null) {
                createDefaultChannel(notificationManager)
        }else if(notificationManager != null){
            createCostumeChannel(context, notificationManager, soundFile)
        }
        val channelId: String = if(soundFile == "default") {
            DEFAULT_CHANNEL_ID
        }else{
            COSTUME_CHANNEL_ID
        }
        val appIconResourceId: Int = context.applicationInfo.icon
        val notificationId: Int = System.currentTimeMillis().toInt()
        val notificationDataBundle = Arguments.toBundle(notificationData)
        val acceptIntentData = Intent(context, NotificationsBroadcastReceiver::class.java)
        acceptIntentData.putExtra("action", "accept")
        acceptIntentData.putExtra("notificationId", notificationId)
        acceptIntentData.putExtra(EXTRA_NOTIFICATION, notificationDataBundle)
        val acceptPendingIntent = PendingIntent.getBroadcast(context, notificationId, acceptIntentData, PendingIntent.FLAG_UPDATE_CURRENT)

        val rejectIntentData = Intent(context, NotificationsBroadcastReceiver::class.java)
        rejectIntentData.putExtra("action", "reject")
        rejectIntentData.putExtra("notificationId", notificationId)
        rejectIntentData.putExtra(EXTRA_NOTIFICATION, notificationDataBundle)
        val rejectPendingIntent = PendingIntent.getBroadcast(context, notificationId + 1, rejectIntentData, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(context, channelId)
                        .addAction(
                                appIconResourceId, "accept",
                                acceptPendingIntent
                        )
                        .addAction(
                                appIconResourceId, "reject",
                                rejectPendingIntent
                        )
                        .setSmallIcon(appIconResourceId)
                        .setSound(getSound(context, soundFile))
                        .setContentTitle(notificationData.getString("title"))
                        .setContentText(notificationData.getString("body"))
        notificationManager?.notify(notificationId, notificationBuilder.build())
    }

    @JvmStatic
    fun showInfoNotification(context: Context, notificationData: ReadableMap, notificationId: Int, soundFile: String) {
        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        if (soundFile == "default" && notificationManager != null) {
            createDefaultChannel(notificationManager)
        }else if(notificationManager != null){
            createCostumeChannel(context, notificationManager, soundFile)
        }
        val channelId: String = if(soundFile == "default") {
            DEFAULT_CHANNEL_ID
        }else{
            COSTUME_CHANNEL_ID
        }
        val appIconResourceId: Int = context.applicationInfo.icon
        val clickIntentData = Intent(context, NotificationsBroadcastReceiver::class.java)
        clickIntentData.putExtra("action", "clicked")
        clickIntentData.putExtra("notificationId", notificationId)
        val notificationDataBundle = Arguments.toBundle(notificationData)
        clickIntentData.putExtra(EXTRA_NOTIFICATION, notificationDataBundle)
        val clickPendingIntent = PendingIntent.getBroadcast(context, notificationId, clickIntentData, PendingIntent.FLAG_UPDATE_CURRENT)
        val title = notificationDataBundle!!.getString("title", "")
        val body = notificationDataBundle.getString("body", "")
        val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(appIconResourceId)
                        .setSound(getSound(context, soundFile))
                        .setContentTitle(title)
                        .setContentText(body)
                        .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                        .setColor(Color.CYAN)
                        .setContentIntent(clickPendingIntent)
        notificationManager?.notify(notificationId, notificationBuilder.build())
    }
}
