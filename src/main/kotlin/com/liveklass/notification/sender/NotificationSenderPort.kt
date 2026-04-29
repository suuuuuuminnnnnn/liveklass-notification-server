package com.liveklass.notification.sender

import com.liveklass.notification.entity.Notification
import com.liveklass.notification.enums.NotificationChannel

interface NotificationSenderPort {
    val channel: NotificationChannel
    fun send(notification: Notification)
}
