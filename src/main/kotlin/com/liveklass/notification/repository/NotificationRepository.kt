package com.liveklass.notification.repository

import com.liveklass.notification.entity.Notification
import com.liveklass.notification.enums.ReadStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByDeduplicationKey(deduplicationKey: String): Notification?
    fun findByRecipientIdOrderByCreatedAtDesc(recipientId: Long): List<Notification>
    fun findByRecipientIdAndReadStatusOrderByCreatedAtDesc(
        recipientId: Long,
        readStatus: ReadStatus,
    ): List<Notification>

    @Query(
        value = """
            SELECT * FROM notifications
            WHERE status = 'READY'
              AND scheduled_at <= CURRENT_TIMESTAMP
              AND next_retry_at <= CURRENT_TIMESTAMP
            ORDER BY created_at ASC
            LIMIT :limit
            FOR UPDATE SKIP LOCKED
        """,
        nativeQuery = true,
    )
    fun findReadyNotificationsWithLock(@Param("limit") limit: Int): List<Notification>

    @Query(
        value = """
            SELECT * FROM notifications
            WHERE status = 'PROCESSING'
              AND locked_at < :threshold
        """,
        nativeQuery = true,
    )
    fun findStuckProcessingNotifications(@Param("threshold") threshold: LocalDateTime): List<Notification>
}