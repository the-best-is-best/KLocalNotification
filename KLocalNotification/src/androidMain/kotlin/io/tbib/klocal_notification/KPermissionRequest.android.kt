package io.tbib.klocal_notification

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted


@OptIn(ExperimentalPermissionsApi::class)
actual class KPermissionRequest actual constructor(
    private val callback: (Boolean) -> Unit,
    requestPermission: Any?
) {
    private val permissionState = requestPermission as? PermissionState

    actual suspend fun launch() {
//
        // If permissionState is null (older SDK), no action needed
        if (permissionState == null) {
            callback(true)
            return
        }

        // If the permission isn't granted, request it
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()

        }


        // Trigger callback based on permission state
        callback(permissionState.status.isGranted)
    }
}
