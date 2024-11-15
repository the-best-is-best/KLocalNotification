package io.tbib.klocal_notification

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

internal class NotificationReceiver : BroadcastReceiver() {
    @SuppressLint("ServiceCast")
    override fun onReceive(context: Context?, intent: Intent?) {
        val data = intent?.getStringExtra("data")
        if (data != null) {
            LocalNotification.notifyNotificationListener(data)
        }
    }
}

