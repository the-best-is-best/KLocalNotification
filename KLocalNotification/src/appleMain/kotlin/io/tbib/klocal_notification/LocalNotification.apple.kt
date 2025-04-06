package io.tbib.klocal_notification

import io.github.native.kiosnotification.KIOSNotification
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol

@OptIn(ExperimentalForeignApi::class)
actual object LocalNotification {
    fun init(userNotificationCenterDelegate: UNUserNotificationCenterDelegateProtocol) {
        KIOSNotification.requestAuthorization()
        UNUserNotificationCenter.currentNotificationCenter().delegate =
            userNotificationCenterDelegate

    }


    actual fun showNotification(config: NotificationConfig) {

        KIOSNotification.showNotificationWithId(
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
        KIOSNotification.setNotificationListenerWithCallback(callback)
    }

    fun notifyNotification(data: Map<Any?, *>?) {
        if (data != null) {
            KIOSNotification.notifyNotificationWithData(data)
        }
    }





}

private fun LocalDateTime.toNSDate(): NSDate {
    val instant = this.toInstant(TimeZone.currentSystemDefault())
    return NSDate.dateWithTimeIntervalSince1970(instant.epochSeconds.toDouble())
}
