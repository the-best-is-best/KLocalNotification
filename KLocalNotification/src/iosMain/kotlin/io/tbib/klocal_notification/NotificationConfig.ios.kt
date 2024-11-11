package io.tbib.klocal_notification

import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.Foundation.timeIntervalSinceNow
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

actual class LocalNotification actual constructor() {
    actual fun showNotification(config: NotificationConfig) {
        println("showNotification")
        val content = UNMutableNotificationContent()
            .apply {
                setTitle(config.title)
                setBody(config.message)
            }


        // Convert LocalDateTime to NSDate for scheduling


        // Set up the trigger based on the `schedule` flag
        val trigger = if (config.schedule) {
            val calendar = NSCalendar.currentCalendar
            val components = NSDateComponents().apply {
                year = config.dateTime.year.toLong()
                month = config.dateTime.monthNumber.toLong()
                day = config.dateTime.dayOfMonth.toLong()
                hour = config.dateTime.hour.toLong()
                minute = config.dateTime.minute.toLong()
                second = config.dateTime.second.toLong()
            }

            val notificationDate = calendar.dateFromComponents(components) ?: NSDate()
            val triggerDate = notificationDate.timeIntervalSinceNow
            UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
                triggerDate,
                repeats = false
            )
        } else {
            UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(1.0, repeats = false)
        }

        // Create a notification request
        val request = UNNotificationRequest.requestWithIdentifier(
            config.id.toString(),
            content,
            trigger
        )

        // Schedule or deliver the notification
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.addNotificationRequest(request) { error ->
            error?.let {
                println("Error scheduling notification: $it")
            }
        }
    }
}