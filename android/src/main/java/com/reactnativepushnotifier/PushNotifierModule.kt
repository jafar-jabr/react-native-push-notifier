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
  fun showInfoPush(notificationData: ReadableMap, notificationId: Int, soundFile: String= "default") {
    NotificationUtils.showInfoNotification(appContext, notificationData, notificationId, soundFile.lowercase(Locale.ENGLISH))
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
    ringtone?.isLooping = true
    ringtone?.play()
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  fun cancelLastNotification() {
    val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancelAll()
  }

  @ReactMethod
  fun stopAlert(s: String) {
    ringtone?.stop()
    cancelLastNotification();
  }

  @ReactMethod
  fun isAppInForeground(promise: Promise) {
    promise.resolve(Utils.isAppInForeground(appContext))
  }

  @ReactMethod
  fun showIncomingCall(notificationData: ReadableMap, promise: Promise) {
    val dictionary: HashMap<String, String> = HashMap<String, String>()
    dictionary.put("title", "hello")
    dictionary["body"] = "there"
    val activity = currentActivity
    if (activity != null) {
      NotificationUtils.showActionNotification(appContext, notificationData, activity, promise)
    }
  }
}
