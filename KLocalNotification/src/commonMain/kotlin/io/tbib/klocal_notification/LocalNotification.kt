package io.tbib.klocal_notification

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
    suspend fun requestAuthorization(): Boolean
    fun removeNotification(notificationId: Int)

    @Deprecated("Will be removed soon use only setNotificationClickedListener")
    fun setNotificationReceivedListener(callback: (Map<Any?, *>) -> Unit)
    fun setNotificationClickedListener(callback: (Map<Any?, *>) -> Unit)


}

