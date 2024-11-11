package io.tbib.klocal_notification

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class NotificationConfig(
    val id: Int,
    val idChannel: String,
    val title: String = "Notification",
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
    fun setNotificationReceivedListener(callback: ((Result<Map<Any?, *>?>) -> Unit)?)
    fun setNotificationClickedListener(callback: ((Result<Map<Any?, *>?>) -> Unit)?)


}

