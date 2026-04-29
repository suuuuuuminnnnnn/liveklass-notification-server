package com.liveklass.notification.entity

import com.liveklass.notification.enums.NotificationChannel
import com.liveklass.notification.enums.NotificationType
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "notification_templates",
    uniqueConstraints = [UniqueConstraint(columnNames = ["notification_type", "channel"])]
)
class NotificationTemplate(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    val notificationType: NotificationType,

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    val channel: NotificationChannel,

    @Column(nullable = false)
    val titleTemplate: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val contentTemplate: String,

    @Column(nullable = false)
    var enabled: Boolean = true,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)
