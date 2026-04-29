package com.liveklass.notification.entity

import com.liveklass.notification.enums.NotificationChannel
import com.liveklass.notification.enums.NotificationStatus
import com.liveklass.notification.enums.NotificationType
import com.liveklass.notification.enums.ReadStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "notifications",
    uniqueConstraints = [UniqueConstraint(columnNames = ["deduplication_key"])]
)
class Notification(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val recipientId: Long,

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    val notificationType: NotificationType,

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    val channel: NotificationChannel,

    @Column(nullable = false)
    val referenceType: String,

    @Column(nullable = false)
    val referenceId: String,

    @Column(nullable = false, unique = true)
    val deduplicationKey: String,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    var status: NotificationStatus = NotificationStatus.READY,

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    var readStatus: ReadStatus = ReadStatus.UNREAD,

    @Column(nullable = false)
    var retryCount: Int = 0,

    @Column(nullable = false)
    val maxRetryCount: Int = 3,

    @Column(nullable = false)
    val scheduledAt: LocalDateTime,

    @Column(nullable = false)
    var nextRetryAt: LocalDateTime,

    var lockedBy: String? = null,
    var lockedAt: LocalDateTime? = null,
    var sentAt: LocalDateTime? = null,
    var readAt: LocalDateTime? = null,
    var failedAt: LocalDateTime? = null,

    @Column(columnDefinition = "TEXT")
    var failureReason: String? = null,

    @Column(nullable = false)
    var manualRetryCount: Int = 0,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {
    fun claim(instanceId: String, now: LocalDateTime) {
        status = NotificationStatus.PROCESSING
        lockedBy = instanceId
        lockedAt = now
        updatedAt = now
    }

    fun markSent(now: LocalDateTime) {
        status = NotificationStatus.SENT
        sentAt = now
        failureReason = null
        lockedBy = null
        lockedAt = null
        updatedAt = now
    }

    fun handleFailure(reason: String, now: LocalDateTime) {
        retryCount++
        failureReason = reason
        lockedBy = null
        lockedAt = null
        updatedAt = now
        if (retryCount >= maxRetryCount) {
            status = NotificationStatus.FAILED
            failedAt = now
        } else {
            status = NotificationStatus.READY
            nextRetryAt = calculateNextRetryAt(retryCount, now)
        }
    }

    fun recoverFromStuck(now: LocalDateTime) {
        lockedBy = null
        lockedAt = null
        updatedAt = now
        if (retryCount >= maxRetryCount) {
            status = NotificationStatus.FAILED
            failedAt = now
            failureReason = "Processing timeout: max retries exceeded"
        } else {
            status = NotificationStatus.READY
            nextRetryAt = now
            failureReason = "Processing timeout: recovered from stuck state"
        }
    }

    fun markAsRead(now: LocalDateTime) {
        if (readStatus == ReadStatus.UNREAD) {
            readStatus = ReadStatus.READ
            readAt = now
            updatedAt = now
        }
    }

    fun prepareManualRetry(now: LocalDateTime) {
        status = NotificationStatus.READY
        retryCount = 0
        manualRetryCount++
        nextRetryAt = now
        lockedBy = null
        lockedAt = null
        failedAt = null
        updatedAt = now
    }

    private fun calculateNextRetryAt(retryCount: Int, now: LocalDateTime): LocalDateTime =
        now.plusSeconds(when (retryCount) {
            1 -> 10L
            2 -> 30L
            else -> 60L
        })
}
