package com.reactnativepushnotifier.utils

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.facebook.react.bridge.ReadableMap
import androidx.core.content.ContextCompat.startActivity

import com.reactnativepushnotifier.R
import java.io.Serializable


object ViewUtils {

   const val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084
  const val EXTRA_NOTIFIER = "com.rn.simple.notifier.clicked"

  const val ACTIVITY_NAME = "activityName"
  const val ACTION = "action"
  const val REJECT = "reject"
  const val ANSWER = "answer"
  const val CALLER_NAME = "callerName"

  @RequiresApi(Build.VERSION_CODES.S)
  fun showCallView(context: Context, data: ReadableMap, activity: Activity) {
    val callerName = data.getString(CALLER_NAME)

    if (checkFloatingWindowPermission(context)) {
      val intent = Intent(activity, ViewService::class.java)
      intent.putExtra(ACTIVITY_NAME, activity.componentName.className)
      intent.putExtra(CALLER_NAME,callerName)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      activity.startService(intent)
    }
      //activity.startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION)

  }
  public fun initializeView(context: Context,activity: Activity) {
    context.startService(Intent(activity, ViewService::class.java))
  }

  private fun checkFloatingWindowPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (Settings.canDrawOverlays(context)) {
        true
      } else {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Toast.makeText(context.applicationContext, R.string.permission_floating_window, Toast.LENGTH_SHORT)
          .show()
        context.startActivity(intent)
        false
      }
    } else {
      true
    }
  }
}
