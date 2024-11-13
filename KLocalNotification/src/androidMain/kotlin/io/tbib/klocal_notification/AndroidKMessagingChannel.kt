package io.tbib.klocal_notification

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import io.github.kpermissions.handler.PermissionHandler
import java.lang.ref.WeakReference

class AndroidKMessagingChannel {
    companion object {
        private var activity: WeakReference<Activity?> = WeakReference(null)


        internal fun getActivity(): Activity {
            return activity.get()!!

        }

        fun initialization(activity: Activity) {
            PermissionHandler.init(activity)
            this.activity = WeakReference(activity)

        }
    }


    // Initialize Notification Channel
    fun initChannel(id: String, name: String, channelDesc: String? = null) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                id,
                name,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = channelDesc ?: "Default channel description"
            }


            val notificationManager =
                getActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}