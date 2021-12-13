package com.reactnativepushnotifier.utils

import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.*
import android.os.Bundle
import com.facebook.react.HeadlessJsTaskService
import android.content.res.Resources.NotFoundException
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri

class NotificationsBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val extras: Bundle? = intent.extras
        if (extras != null) {
            val notification =
                    intent.getParcelableExtra(NotificationUtils.EXTRA_NOTIFICATION) as Bundle?
            if (notification != null) {
                val action = intent.getStringExtra("action")
                val notificationId = intent.getIntExtra("notificationId", 1)
                val mainActivityResId = context.resources.getIdentifier("main_activity_name", "string", context.packageName)
                val mainActivityClassName: String = try {
                 context.getString(mainActivityResId)
                } catch (e: NotFoundException) {
                  "null"
                }
                val appIntent = Intent(context, Class.forName(mainActivityClassName))
                val contentIntent: PendingIntent = getActivity(context, 0, appIntent, FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT)
                try {
                    contentIntent.send()
                } catch (e: CanceledException) {
                    e.printStackTrace()
                }
                /**
                 * collapse notification bar
                 */
                val closeIntent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                context.sendBroadcast(closeIntent)
                /**
                 * ###################################
                 */
                try{
                    val headlessIntent = Intent(
                            context,
                            NotificationsHeadlessReceiver::class.java
                    )
                    notification.putString("action", action)
                    notification.putInt("notificationId", notificationId)
                    headlessIntent.putExtra(NotificationUtils.EXTRA_NOTIFICATION, notification)
                    val name: ComponentName? = context.startService(headlessIntent)
                    if (name != null && action == "clicked") {
                        HeadlessJsTaskService.acquireWakeLockNow(context)
                    } else {
                      val costumeSound = ResourcesResolver(context).getRaw("wm_ringtone")
                      val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://" + context.packageName + "/" + costumeSound)
                      val ringtone: Ringtone = RingtoneManager.getRingtone(context, soundUri)
                      ringtone.stop()
                    }
                }catch (ignored: IllegalStateException){
                }
            }
        }
    }
}
