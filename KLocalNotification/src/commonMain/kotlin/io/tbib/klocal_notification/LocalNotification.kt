package io.tbib.klocal_notification

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class NotificationConfig(
    val id: Int,
    val idChannel: String,
    val title: String,
    val message: String,
    val smallIcon: String,
    val schedule: Boolean = false,
    val dateTime: LocalDateTime = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()),
    val data: Map<Any?, *>? = null
)


expect object LocalNotification {
    fun showNotification(config: NotificationConfig)


    fun removeNotification(notificationId: Int)


    val payloadFlow: SharedFlow<Map<Any?, *>>

}

