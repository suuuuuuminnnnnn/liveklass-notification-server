package com.liveklass.notification.dto

import com.liveklass.notification.entity.Notification
import com.liveklass.notification.enums.NotificationChannel
import com.liveklass.notification.enums.NotificationStatus
import com.liveklass.notification.enums.NotificationType
import com.liveklass.notification.enums.ReadStatus
import java.time.LocalDateTime

data class UserNotificationResponse(
    val id: Long,
    val type: NotificationType,
    val channel: NotificationChannel,
    val title: String,
    val content: String,
    val status: NotificationStatus,
    val readStatus: ReadStatus,
    val createdAt: LocalDateTime,
    val sentAt: LocalDateTime?,
) {
    companion object {
        fun from(notification: Notification): UserNotificationResponse =
            UserNotificationResponse(
                id = notification.id,
                type = notification.notificationType,
                channel = notification.channel,
                title = notification.title,
                content = notification.content,
                status = notification.status,
                readStatus = notification.readStatus,
                createdAt = notification.createdAt,
                sentAt = notification.sentAt,
            )
    }
}
