package io.tbib.klocal_notification

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun LocalNotificationRequestAuthorization(callback: (Boolean) -> Unit): KPermissionRequest {
    val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }

    return remember { KPermissionRequest(callback, permissionState) }
}

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
