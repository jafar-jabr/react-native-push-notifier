package com.reactnativepushnotifier.utils

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class PushForegroundService : Service() {

  private val binder: Binder = PushBinder()

  override fun onBind(p0: Intent?): IBinder = binder

  inner class PushBinder : Binder() {
    val service: PushForegroundService
      get() = this@PushForegroundService
  }
}
