package com.liveklass.notification.dto

import com.liveklass.notification.enums.NotificationChannel
import com.liveklass.notification.enums.NotificationType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class CreateNotificationRequest(
    @field:NotNull val recipientId: Long,
    @field:NotNull val type: NotificationType,
    @field:NotNull val channel: NotificationChannel,
    @field:NotBlank val referenceType: String,
    @field:NotBlank val referenceId: String,
    val templateVariables: Map<String, String> = emptyMap(),
    val scheduledAt: LocalDateTime? = null,
)
