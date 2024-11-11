package io.tbib.klocal_notification

import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.tbib.klocal_notification.LocalNotification.notificationListener

class MyNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) {
            Log.d("MyNotificationListener", "Notification sbn is null")
            return
        }

        Log.d("MyNotificationListener", "Notification posted: ${sbn.notification.extras}")

        val extras: Bundle = sbn.notification.extras
        val dataJson = extras.getString("data")
        if (dataJson != null) {
            Log.d("MyNotificationListener", "Data received: $dataJson")
            val gson = Gson()
            val type = object : TypeToken<Map<String, String>>() {}.type
            val customData = gson.fromJson<Map<Any?, *>?>(dataJson, type)
            notificationListener?.invoke(Result.success(customData))
            LocalNotification.setNotificationReceivedListener(notificationListener)
        } else {
            Log.d("MyNotificationListener", "No data extra in notification")
        }
    }


    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        sbn?.let {
        }
    }
}
