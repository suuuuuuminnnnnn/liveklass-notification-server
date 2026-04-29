package com.liveklass.notification.repository

import com.liveklass.notification.entity.NotificationTemplate
import com.liveklass.notification.enums.NotificationChannel
import com.liveklass.notification.enums.NotificationType
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationTemplateRepository : JpaRepository<NotificationTemplate, Long> {
    fun findByNotificationTypeAndChannelAndEnabledTrue(
        notificationType: NotificationType,
        channel: NotificationChannel,
    ): NotificationTemplate?
}
