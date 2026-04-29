package com.liveklass.notification.sender

import com.liveklass.notification.entity.Notification
import org.springframework.stereotype.Component

@Component
class NotificationSenderRouter(senders: List<NotificationSenderPort>) {
    private val senderMap = senders.associateBy { it.channel }

    fun send(notification: Notification) {
        val sender = senderMap[notification.channel]
            ?: throw IllegalArgumentException("지원하지 않는 채널: ${notification.channel}")
        sender.send(notification)
    }
}