package io.tbib.klocal_notification

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat

class NotificationScheduleReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver", "SuspiciousIndentation", "MissingPermission")
    override fun onReceive(ctx: Context, intent: Intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ctx.startForegroundService(intent)
        } else {
            ctx.startService(intent)
        }
        val activityName: String = intent.getStringExtra("activityName")!!
        val clazz = Class.forName(activityName)

        val clickIntent = Intent(ctx, clazz).apply {

            putExtra("data", intent.getStringExtra("data"))
        }
        val clickPendingIntent = PendingIntent.getActivity(
            ctx,
            0,
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title: String? = intent.getStringExtra("title")
        val message: String? = intent.getStringExtra("message")
        val id: Int = intent.getIntExtra("id", 0)
        val idChannel: String? = intent.getStringExtra("idChannel")
        val smallIcon: String = intent.getStringExtra("icon") ?: ""
        val configJson = intent.getStringExtra("data")


        if (title != null && message != null) {
            val notification = LocalNotification.notifyNotification(
                ctx,
                idChannel!!,
                title,
                message,
                smallIcon,
                clickPendingIntent
            )
            NotificationManagerCompat.from(ctx).notify(id, notification)
        }

    }
}
