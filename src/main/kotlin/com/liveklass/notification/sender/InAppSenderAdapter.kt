package com.liveklass.notification.sender

import com.liveklass.notification.entity.Notification
import com.liveklass.notification.enums.NotificationChannel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class InAppSenderAdapter : NotificationSenderPort {
    private val log = LoggerFactory.getLogger(javaClass)
    override val channel = NotificationChannel.IN_APP

    override fun send(notification: Notification) {
        log.info("[IN_APP] notificationId={}", notification.id)
    }
}
