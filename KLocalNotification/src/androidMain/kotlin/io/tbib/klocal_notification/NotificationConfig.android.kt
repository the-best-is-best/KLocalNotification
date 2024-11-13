package io.tbib.klocal_notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.kpermissions.enum.EnumAppPermission
import io.github.kpermissions.handler.PermissionHandler
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Calendar
import kotlin.coroutines.resume

actual object LocalNotification {
    private var notificationListener: ((Map<Any?, *>) -> Unit)? = null
    private var notificationClickedListener: ((Map<Any?, *>) -> Unit)? = null

    @SuppressLint("LaunchActivityFromNotification", "MissingPermission", "SuspiciousIndentation")
    actual fun showNotification(config: NotificationConfig) {

        val gson = Gson()
        val configJson = if (config.data != null) gson.toJson(config.data) else null
        val context = AndroidKMessagingChannel.getActivity()
        val intent = Intent(
            context.applicationContext,
            if (config.schedule) NotificationScheduleReceiver::class.java else NotificationReceiver::class.java
        ).apply {
            putExtra("title", config.title)
            putExtra("message", config.message)
            putExtra("id", config.id)
            putExtra("idChannel", config.idChannel)
            putExtra("icon", config.smallIcon)
            putExtra("data", configJson)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context.applicationContext,
            config.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
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
                .setAutoCancel(true)
                .build()



            NotificationManagerCompat.from(context).notify(config.id, notification)
            if (configJson != null)
                notifyReceivedNotificationListener(configJson)

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
        val intent = Intent(context, NotificationScheduleReceiver::class.java)
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

    fun notifyReceivedNotificationListener(dataJson: String) {
        val type =
            object :
                TypeToken<Map<String, String>>() {}.type // Define the type for deserialization
        val yourDataMap: Map<Any?, *> =
            Gson().fromJson(dataJson, type) // Deserialize back to a map
        if (yourDataMap.isNotEmpty()) {
            notificationListener?.invoke(yourDataMap)
        }

    }

    fun notifyNotificationClickedListener(dataJson: String? = null) {
        if (dataJson != null) {


            val type =
                object :
                    TypeToken<Map<String, String>>() {}.type // Define the type for deserialization
            val yourDataMap: Map<Any?, *> =
                Gson().fromJson(dataJson, type) // Deserialize back to a map
            if (yourDataMap.isNotEmpty()) {
                notificationClickedListener?.invoke(yourDataMap)
            }
        }
    }

    fun notifyNotificationOpenAppClicked(dataJson: String? = null) {
        Handler(Looper.getMainLooper()).postDelayed({

            notifyNotificationClickedListener(dataJson)
        }, 500)

    }
//    fun notifyNotificationBackgroundClicked(dataBundle: Bundle) {
//        if (!dataBundle.isEmpty()) {
//            // Create a map to store the key-value pairs
//            val dataMap = mutableMapOf<String, String>()
//
//            // Iterate over the keys in the Bundle (extras)
//            for (key in dataBundle.keySet()) {
//                // Get the value associated with the key
//                val value = dataBundle.getString(key)
//                // Add to the map if the value is not null
//                if (value != null) {
//                    dataMap[key] = value
//                }
//            }
//
//            // Convert the map to a JSON string using Gson
//            val jsonString = Gson().toJson(dataMap)
//            notifyNotificationClickedListener(jsonString)
//
//        }
    // }

    actual suspend fun requestAuthorization(): Boolean {
        return suspendCancellableCoroutine { cont ->

            val permission = PermissionHandler()
            permission.requestPermission(EnumAppPermission.NOTIFICATION) { granted ->
                cont.resume(granted)
            }
        }
    }

}

@SuppressLint("DiscouragedApi")
fun getIconResourceIdByName(iconName: String): Int {
    val context = AndroidKMessagingChannel.getActivity()
    return context.resources.getIdentifier(iconName, "drawable", context.packageName)

}
