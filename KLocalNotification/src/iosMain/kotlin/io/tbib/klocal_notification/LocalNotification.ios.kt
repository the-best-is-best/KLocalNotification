package io.tbib.klocal_notification

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.Foundation.timeIntervalSinceNow
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationState
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

actual object LocalNotification {
    private var notificationListener: ((Map<Any?, *>) -> Unit)? = null

    private var lastNotificationData: Map<Any?, *>? = null

    @OptIn(DelicateCoroutinesApi::class)
    fun init(userNotificationCenterDelegate: UNUserNotificationCenterDelegateProtocol) {
        GlobalScope.launch {
            requestAuthorization()
        }
        UNUserNotificationCenter.currentNotificationCenter().delegate =
            userNotificationCenterDelegate

    }

    fun showNotificationIos(
        id: String,
        content: UNNotificationContent,
        trigger: UNNotificationTrigger? = null
    ) {
        // Create a notification request
        val request = UNNotificationRequest.requestWithIdentifier(
            id,
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

    actual fun showNotification(config: NotificationConfig) {
        val content = UNMutableNotificationContent()
            .apply {
                setTitle(config.title)
                setBody(config.message)
                config.data?.let { setUserInfo(it) }
            }

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
        showNotificationIos(config.id.toString(), content, trigger)

    }

    actual fun removeNotification(notificationId: Int) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.removePendingNotificationRequestsWithIdentifiers(listOf(notificationId.toString()))
    }

    actual fun setNotificationListener(callback: (Map<Any?, *>) -> Unit){

        notificationListener = callback
        lastNotificationData?.let {
            callback(it)
        }
        lastNotificationData = null

    }

    fun notifyNotification(data: Map<Any?, *>?) {
        if (data != null) {
            lastNotificationData = data
            notificationListener?.invoke(data)
        }
    }


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