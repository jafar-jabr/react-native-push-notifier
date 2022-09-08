package com.reactnativepushnotifier.utils

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder

private lateinit var pushForegroundService: PushForegroundService
private var isTimerServiceBound: Boolean = false

private val ServiceConnection = object : ServiceConnection {

  override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
    pushForegroundService = (binder as PushForegroundService.PushBinder).service
    isTimerServiceBound = true
  }

  override fun onServiceDisconnected(p0: ComponentName?) {
    isTimerServiceBound = false
  }
}
