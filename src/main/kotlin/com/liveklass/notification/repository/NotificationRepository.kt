package com.liveklass.notification.repository

import com.liveklass.notification.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByDeduplicationKey(deduplicationKey: String): Notification?
}
