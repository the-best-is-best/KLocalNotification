package io.github.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kpermissions.enum.EnumAppPermission
import io.github.kpermissions.handler.PermissionHandler
import io.github.sample.theme.AppTheme
import io.tbib.klocal_notification.LocalNotification
import io.tbib.klocal_notification.NotificationConfig
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun App() = AppTheme {
    val permission = PermissionHandler()
    LaunchedEffect(Unit) {
        permission.requestPermission(EnumAppPermission.NOTIFICATION) {
            println("permission $it")
            if (!it) {
                permission.openAppSettings()
            }
        }
    }
    LocalNotification.setNotificationReceivedListener {
        println("notification received is $it")
    }
    LocalNotification.setNotificationClickedListener {
        println("notification clicked data is $it")
    }
    var notificationId: Int? = null
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            notificationId = Random.nextInt().absoluteValue
            println("id is $notificationId")
            val currentDateTme =
                Clock.System.now().plus(15.seconds).toLocalDateTime(TimeZone.currentSystemDefault())
            LocalNotification.showNotification(
                config = NotificationConfig(
                    id = notificationId!!,
                    idChannel = "reminder",
                    title = "Test title",
                    message = "Test Message",
                    smallIcon = "ic_notification",
                    data = mapOf("test" to 1),
                    schedule = false,
                    dateTime = currentDateTme,
                )
            )
        }) {
            Text("Show notification")
        }

        Button(onClick = {
            println("id is removed $notificationId")

            if (notificationId != null)
                LocalNotification.removeNotification(notificationId!!)
        }) {
            Text("remove notification")
        }
    }
}
