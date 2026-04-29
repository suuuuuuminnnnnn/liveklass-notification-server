package com.liveklass.notification.dto

import com.liveklass.notification.enums.NotificationStatus

data class CreateNotificationResponse(
    val notificationId: Long,
    val status: NotificationStatus,
    val duplicated: Boolean,
)
