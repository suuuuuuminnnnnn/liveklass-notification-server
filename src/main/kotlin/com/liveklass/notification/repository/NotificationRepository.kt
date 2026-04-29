package com.liveklass.notification.repository

import com.liveklass.notification.entity.Notification
import com.liveklass.notification.enums.ReadStatus
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByDeduplicationKey(deduplicationKey: String): Notification?
    fun findByRecipientIdOrderByCreatedAtDesc(recipientId: Long): List<Notification>
    fun findByRecipientIdAndReadStatusOrderByCreatedAtDesc(
        recipientId: Long,
        readStatus: ReadStatus,
    ): List<Notification>
}