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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.sample.theme.AppTheme
import io.tbib.klocal_notification.LocalNotification
import io.tbib.klocal_notification.LocalNotificationRequestAuthorization
import io.tbib.klocal_notification.NotificationConfig
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun App() = AppTheme {
    var dataNotification by remember { mutableStateOf<Map<Any?, *>>(mapOf("" to "")) }
    LaunchedEffect(Unit) {

        LocalNotification.payloadFlow.collect {
            println("notification received is $it")
            dataNotification = it
        }
    }


    val localNotificationRequest = LocalNotificationRequestAuthorization {
        println("permission is $it")
    }

    LaunchedEffect(Unit) {
        localNotificationRequest.launch()
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
                Clock.System.now().plus(5.seconds).toLocalDateTime(TimeZone.currentSystemDefault())
            LocalNotification.showNotification(
                config = NotificationConfig(
                    id = notificationId!!,
                    idChannel = "reminder",
                    title = "Test title",
                    message = "Test Message",
                    smallIcon = "ic_notification",
                    data = mapOf("test" to "test notification 2"),
                    schedule = true,
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
        Text("notification received is $dataNotification")

    }


}
