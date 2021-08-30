package com.reactnativepushnotifier

import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.facebook.react.bridge.*
import com.reactnativepushnotifier.utils.NotificationUtils
import com.reactnativepushnotifier.utils.ResourcesResolver
import com.reactnativepushnotifier.utils.Utils
import java.util.*

class PushNotifierModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

  private val appContext: Context = reactContext.applicationContext
  private var mNotificationManager: NotificationManager? = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
  private var ringtone: Ringtone? = null
    override fun getName(): String {
        return "PushNotifier"
    }

  @ReactMethod
  fun showActionPush(notificationData: ReadableMap, soundFile: String) {
    NotificationUtils.showActionNotification(appContext, notificationData, soundFile.toLowerCase(Locale.ENGLISH))
  }

  @ReactMethod
  fun showInfoPush(notificationData: ReadableMap, notificationId: Int, soundFile: String= "default") {
    NotificationUtils.showInfoNotification(appContext, notificationData, notificationId, soundFile.toLowerCase(Locale.ENGLISH))
  }
  @ReactMethod
  fun removeNotification(notificationId: Int) {
    mNotificationManager?.cancel(notificationId)
  }
  @ReactMethod
  fun clearNotifications() {
    mNotificationManager?.cancelAll()
  }

  @RequiresApi(Build.VERSION_CODES.P)
  @ReactMethod
  fun runAlert(sound: String="default") {
    ringtone?.stop()
    val soundFile = sound.lowercase(Locale.ENGLISH)
    val soundUri: Uri = if(soundFile == "default") {
      RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    }else{
      val costumeSound = ResourcesResolver(appContext).getRaw(sound)
      Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://" + appContext.packageName + "/" + costumeSound)
    }
    ringtone = RingtoneManager.getRingtone(appContext, soundUri)
    ringtone?.setLooping(true)
    ringtone?.play()
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  fun cancelLastNotification() {
    val notificationManager =
      appContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    val notificationArray = notificationManager?.activeNotifications
    if(notificationArray?.isEmpty()!!) {
      val lastNotificationId = notificationArray.last()?.id
      if (lastNotificationId != null) {
        notificationManager.cancel(lastNotificationId)
      }
    }
  }

  @ReactMethod
  fun stopAlert(s: String) {
    ringtone?.stop()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      cancelLastNotification();
    }
  }

  @ReactMethod
  fun isAppInForeground(promise: Promise) {
    promise.resolve(Utils.isAppInForeground(appContext))
  }
}
