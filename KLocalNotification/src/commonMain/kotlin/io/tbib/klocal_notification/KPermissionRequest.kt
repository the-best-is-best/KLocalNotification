package io.tbib.klocal_notification

import androidx.compose.runtime.Composable

@Composable
expect fun LocalNotificationRequestAuthorization(callback: (Boolean) -> Unit): KPermissionRequest

expect class KPermissionRequest(
    callback: (Boolean) -> Unit,
    requestPermission: Any?
) {
    suspend fun launch()
}