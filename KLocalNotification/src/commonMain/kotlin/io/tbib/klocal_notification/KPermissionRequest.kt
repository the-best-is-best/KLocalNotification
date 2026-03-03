package io.tbib.klocal_notification

expect class KPermissionRequest(
    callback: (Boolean) -> Unit,
    requestPermission: Any?
) {
    suspend fun launch()
}