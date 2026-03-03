package io.tbib.klocal_notification

import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter


actual class KPermissionRequest actual constructor
    (private val callback: (Boolean) -> Unit, requestPermission: Any?) {

    actual suspend fun launch() {
        val center = UNUserNotificationCenter.currentNotificationCenter()

        center.requestAuthorizationWithOptions(
            options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { granted, error ->
            if (error != null) {
                callback(false)
            } else {
                callback(granted)
            }
        }
    }
}