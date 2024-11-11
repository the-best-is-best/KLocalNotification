package io.tbib.klocal_notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

actual object LocalNotification {
    internal var notificationListener: ((Map<Any?, *>) -> Unit)? = null
    private var notificationClickedListener: ((Map<Any?, *>) -> Unit)? = null

    @SuppressLint("LaunchActivityFromNotification")
    actual fun showNotification(config: NotificationConfig) {

        val gson = Gson()
        val configJson = if (config.data != null) gson.toJson(config.data) else null
        val context = AndroidKMessagingChannel.getActivity()
        val intent = Intent(context.applicationContext, NotificationReceiver::class.java).apply {
            putExtra("title", config.title)
            putExtra("message", config.message)
            putExtra("id", config.id)
            putExtra("idChannel", config.idChannel)
            putExtra("icon", config.smallIcon)
            putExtra("data", configJson)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK


        }

        // Set PendingIntent with the appropriate flag
        val pendingIntentFlag =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            config.id,
            intent,
            pendingIntentFlag
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Calculate the alarm trigger time
        val calendar = Calendar.getInstance()
        calendar.set(
            config.dateTime.year,
            config.dateTime.monthNumber - 1,  // Adjust month to 0-based index
            config.dateTime.dayOfMonth,
            config.dateTime.hour,
            config.dateTime.minute,
            config.dateTime.second
        )
        if (config.schedule) {
            // Schedule the notification at a specific time
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            // Show the notification immediately
            val notification = NotificationCompat.Builder(context, config.idChannel)
                .setContentTitle(config.title)
                .setContentText(config.message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setSmallIcon(getIconResourceIdByName(config.smallIcon))
                .setAutoCancel(false)

                .build()

            NotificationManagerCompat.from(context).notify(config.id, notification)
            if (config.data != null) {
                notificationListener?.invoke(config.data)
            }

        }
    }

    actual fun removeNotification(notificationId: Int) {
        val notificationManager =
            NotificationManagerCompat.from(AndroidKMessagingChannel.getActivity())
        notificationManager.cancel(notificationId)
        val context = AndroidKMessagingChannel.getActivity()
        val alarmManager = AndroidKMessagingChannel.getActivity()
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Notification = BroadcastReceiver class
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    actual fun setNotificationReceivedListener(callback: (Map<Any?, *>) -> Unit) {
        notificationListener = callback
    }

    actual fun setNotificationClickedListener(callback: (Map<Any?, *>) -> Unit) {
        notificationClickedListener = callback
    }

    fun notifyNotificationClickedListener(dataJson: String) {
        Handler(Looper.getMainLooper()).postDelayed({

            val type =
                object :
                    TypeToken<Map<String, String>>() {}.type // Define the type for deserialization
            val yourDataMap: Map<Any?, *> =
                Gson().fromJson(dataJson, type) // Deserialize back to a map
            if (yourDataMap.isNotEmpty()) {
                notificationClickedListener?.invoke(yourDataMap)
            }
        }, 500)
    }

}

@SuppressLint("DiscouragedApi")
fun getIconResourceIdByName(iconName: String): Int {
    val context = AndroidKMessagingChannel.getActivity()
    return context.resources.getIdentifier(iconName, "drawable", context.packageName)

}
