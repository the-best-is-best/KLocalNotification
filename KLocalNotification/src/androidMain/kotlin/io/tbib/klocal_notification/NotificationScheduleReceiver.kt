package io.tbib.klocal_notification

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class NotificationScheduleReceiver : BroadcastReceiver() {
    @SuppressLint("ServiceCast")
    override fun onReceive(context: Context?, intent: Intent?) {
        val activity = AndroidKMessagingChannel.getActivity()
        val clickIntent = Intent(context, activity::class.java).apply {
            putExtra("data", intent?.getStringExtra("data"))
        }
        val clickPendingIntent = PendingIntent.getActivity(
            context,
            0,
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title: String? = intent?.getStringExtra("title")
        val message: String? = intent?.getStringExtra("message")
        val id: Int = intent?.getIntExtra("id", 0) ?: 0
        val idChannel: String = intent?.getStringExtra("idChannel") ?: "default_channel"
        val smallIcon: String = intent?.getStringExtra("icon") ?: ""
        val configJson = intent?.getStringExtra("data")






        if (title != null && message != null) {
            val notificationManager =
                activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notification = NotificationCompat.Builder(activity, idChannel)
                .setSmallIcon(getIconResourceIdByName(smallIcon))
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(clickPendingIntent) // Set the click action here
                .setAutoCancel(true) // Automatically dismiss the notification on click
                .build()

            notificationManager.notify(id, notification)
            if (configJson != null)
                LocalNotification.notifyReceivedNotificationListener(configJson)
        }
    }
}

