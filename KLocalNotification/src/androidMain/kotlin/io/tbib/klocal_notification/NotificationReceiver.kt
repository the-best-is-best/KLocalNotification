package io.tbib.klocal_notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


// Receiver to handle notifications when the alarm triggers
class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Extract the notification details from the intent
        val title: String? = intent?.getStringExtra("title")
        val message: String? = intent?.getStringExtra("message")
        val id: Int = intent?.getIntExtra("id", 0) ?: 0
        val idChannel: String = intent?.getStringExtra("idChannel") ?: "default_channel"
        val smallIcon: String = intent?.getStringExtra("icon") ?: ""
        val configJson = intent?.getStringExtra("data")


        val activityIntent =
            Intent(context, AndroidKMessagingChannel.getActivity()::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            activityIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE
            else PendingIntent.FLAG_UPDATE_CURRENT
        )
        // Show the notification
        val notification = NotificationCompat.Builder(context!!, idChannel)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setSmallIcon(getIconResourceIdByName(smallIcon))

            .build()

        // Show the notification using the NotificationManager
        NotificationManagerCompat.from(context).notify(id, notification)
        configJson?.let { LocalNotification.notifyNotificationClickedListener(it) }

        val data = intent?.getStringExtra("data") // Get the data from the notification intent
        val launchIntent =
            Intent(context, AndroidKMessagingChannel.getActivity()::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("data", data) // Pass the data to AppActivity
            }
        context.startActivity(launchIntent)
    }
}
