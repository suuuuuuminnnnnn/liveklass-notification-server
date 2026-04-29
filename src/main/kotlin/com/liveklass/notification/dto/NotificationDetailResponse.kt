package com.liveklass.notification.dto

import com.liveklass.notification.entity.Notification
import com.liveklass.notification.enums.NotificationChannel
import com.liveklass.notification.enums.NotificationStatus
import com.liveklass.notification.enums.NotificationType
import com.liveklass.notification.enums.ReadStatus
import java.time.LocalDateTime

data class NotificationDetailResponse(
    val id: Long,
    val recipientId: Long,
    val type: NotificationType,
    val channel: NotificationChannel,
    val referenceType: String,
    val referenceId: String,
    val title: String,
    val content: String,
    val status: NotificationStatus,
    val readStatus: ReadStatus,
    val retryCount: Int,
    val maxRetryCount: Int,
    val failureReason: String?,
    val createdAt: LocalDateTime,
    val sentAt: LocalDateTime?,
    val failedAt: LocalDateTime?,
) {
    companion object {
        fun from(notification: Notification): NotificationDetailResponse =
            NotificationDetailResponse(
                id = notification.id,
                recipientId = notification.recipientId,
                type = notification.notificationType,
                channel = notification.channel,
                referenceType = notification.referenceType,
                referenceId = notification.referenceId,
                title = notification.title,
                content = notification.content,
                status = notification.status,
                readStatus = notification.readStatus,
                retryCount = notification.retryCount,
                maxRetryCount = notification.maxRetryCount,
                failureReason = notification.failureReason,
                createdAt = notification.createdAt,
                sentAt = notification.sentAt,
                failedAt = notification.failedAt,
            )
    }
}
