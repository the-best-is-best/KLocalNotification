package io.tbib.klocal_notification_compose

import androidx.compose.runtime.Composable
import io.tbib.klocal_notification.KPermissionRequest

@Composable
actual fun LocalNotificationRequestAuthorization(callback: (Boolean) -> Unit): KPermissionRequest {
    return KPermissionRequest(callback, null)

}