package io.tbib.klocal_notification_compose

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import io.tbib.klocal_notification.KPermissionRequest


@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("ComposableNaming")
@Composable
actual fun LocalNotificationRequestAuthorization(callback: (Boolean) -> Unit): KPermissionRequest {
    val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }

    return remember { KPermissionRequest(callback, permissionState) }
}
