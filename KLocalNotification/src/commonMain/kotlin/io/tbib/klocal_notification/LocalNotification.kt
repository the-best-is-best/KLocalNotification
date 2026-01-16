package io.tbib.klocal_notification

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class NotificationConfig @OptIn(ExperimentalTime::class) constructor(
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

    fun removeDataLister()

}

