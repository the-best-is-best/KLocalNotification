package io.tbib.klocal_notification

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
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
//    private var notificationListener: ((Map<Any?, *>) -> Unit)? = null
    private var notificationListener: ((Map<Any?, *>) -> Unit)? = null

//    private var lastNotificationData: Map<Any?, *>? = null
    private var lastNotificationListener: Map<Any?, *>? = null


    @SuppressLint("MissingPermission")
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
            putExtra("activityName", AndroidKMessagingChannel.getActivity().javaClass.name)
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } else {

            val notification = notifyNotification(
                context,
                config.idChannel,
                config.title,
                config.message,
                config.smallIcon,
                pendingIntent
            )
            NotificationManagerCompat.from(context).notify(config.id, notification)

        }
    }

    @SuppressLint("MissingPermission")
    fun notifyNotification(
        context: Context,
        idChannel: String,
        title: String,
        message: String,
        smallIcon: String,
        pendingIntent: PendingIntent? = null
    ): Notification {
        return NotificationCompat.Builder(context, idChannel)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setSmallIcon(getIconResourceIdByName(context, smallIcon))
            .setAutoCancel(true)
            .build()


    }


    actual fun removeNotification(notificationId: Int) {
        val notificationManager =
            NotificationManagerCompat.from(AndroidKMessagingChannel.getActivity())
        notificationManager.cancel(notificationId)
        val context = AndroidKMessagingChannel.getActivity()
        val alarmManager = AndroidKMessagingChannel.getActivity()
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationScheduleReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }



   actual fun setNotificationListener(callback: (Map<Any?, *>?) -> Unit){
       // If there's saved data, trigger the callback immediately
       notificationListener = callback
       lastNotificationListener?.let {
           callback(it)
       }
       lastNotificationListener = null
   }

    fun notifyNotificationListener(dataJson:String?){
        if (dataJson != null) {
            val type =
                object :
                    TypeToken<Map<String, String>>() {}.type // Define the type for deserialization
            val yourDataMap: Map<Any?, *> =
                Gson().fromJson(dataJson, type) // Deserialize back to a map
            if (yourDataMap.isNotEmpty()) {
                lastNotificationListener = yourDataMap
                notificationListener?.invoke(yourDataMap)

            }

        }
    }



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
fun getIconResourceIdByName(context: Context, iconName: String): Int {
    return context.resources.getIdentifier(iconName, "drawable", context.packageName)

}
