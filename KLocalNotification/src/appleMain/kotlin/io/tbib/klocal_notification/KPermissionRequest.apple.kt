package io.tbib.klocal_notification

import androidx.compose.runtime.Composable
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter

@Composable
actual fun LocalNotificationRequestAuthorization(callback: (Boolean) -> Unit): KPermissionRequest {
    return KPermissionRequest(callback, null)

}

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