package io.tbib.klocal_notification

import io.github.native.kiosnotification.KIOSNotification
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol

@OptIn(ExperimentalForeignApi::class)
actual object LocalNotification {
    private val _payloadFlow = MutableSharedFlow<Map<Any?, *>>(replay = 1, extraBufferCapacity = 1)

    fun init(userNotificationCenterDelegate: UNUserNotificationCenterDelegateProtocol) {
        KIOSNotification.requestAuthorization()
        UNUserNotificationCenter.currentNotificationCenter().delegate =
            userNotificationCenterDelegate

    }


    actual fun showNotification(config: NotificationConfig) {

        KIOSNotification.showNotificationWithId(
            config.id.toString(),
            config.title,
            config.message,
            config.schedule,
            config.dateTime.toNSDate(),
            config.data,

            )

    }

    actual fun removeNotification(notificationId: Int) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.removePendingNotificationRequestsWithIdentifiers(listOf(notificationId.toString()))
    }

    fun notifyPayloadListeners(data: Map<Any?, *>) {
        _payloadFlow.tryEmit(data)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    actual fun removeDataLister() {
        _payloadFlow.resetReplayCache()
    }


    actual val payloadFlow: SharedFlow<Map<Any?, *>> = _payloadFlow


}

private fun LocalDateTime.toNSDate(): NSDate {
    val instant = this.toInstant(TimeZone.currentSystemDefault())
    return NSDate.dateWithTimeIntervalSince1970(instant.epochSeconds.toDouble())
}
