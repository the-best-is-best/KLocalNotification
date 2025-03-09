package io.tbib.klocal_notification

import io.github.native.my_local_notification.MyLocalNotification
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.Foundation.NSTimeZone
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.secondsFromGMT
import platform.Foundation.systemTimeZone
import platform.Foundation.timeIntervalSinceNow
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationTrigger
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
actual object LocalNotification {
    fun init(userNotificationCenterDelegate: UNUserNotificationCenterDelegateProtocol) {
        MyLocalNotification.requestAuthorization()
        UNUserNotificationCenter.currentNotificationCenter().delegate =
            userNotificationCenterDelegate

    }


    actual fun showNotification(config: NotificationConfig) {

        MyLocalNotification.showNotificationWithId(
            config.id.toString(),
            config.title,
            config.message,
            config.schedule,
            config.dateTime.toNSDate(),
            config.data,

            )

    }

    actual fun removeNotification(notificationId: Int) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.removePendingNotificationRequestsWithIdentifiers(listOf(notificationId.toString()))
    }

    actual fun setNotificationListener(callback: (Map<Any?, *>?) -> Unit){
        MyLocalNotification.setNotificationListenerWithCallback(callback)
    }

    fun notifyNotification(data: Map<Any?, *>?) {
        if (data != null) {
           MyLocalNotification.notifyNotificationWithData(data)
        }
    }


    @OptIn(UnsafeNumber::class)
    actual suspend fun requestAuthorization(): Boolean {
        return suspendCancellableCoroutine { cont ->

            val center = UNUserNotificationCenter.currentNotificationCenter()

            center.requestAuthorizationWithOptions(
                options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge,
                completionHandler = { granted, error ->
                    if (error != null) {
                        // Handle error
                        cont.resume(false)
                    } else if (granted) {
                        // Permission granted
                        cont.resume(granted)
                    } else {
                        // Permission denied
                        cont.resume(false)
                    }
                }
            )

        }
    }


}

private fun LocalDateTime.toNSDate(): NSDate {
    val instant = this.toInstant(TimeZone.currentSystemDefault())
    return NSDate.dateWithTimeIntervalSince1970(instant.epochSeconds.toDouble())
}
