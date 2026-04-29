package com.liveklass.notification.controller

import com.liveklass.notification.dto.CreateNotificationRequest
import com.liveklass.notification.dto.CreateNotificationResponse
import com.liveklass.notification.dto.NotificationDetailResponse
import com.liveklass.notification.service.NotificationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService,
) {
    @PostMapping
    fun create(@Valid @RequestBody request: CreateNotificationRequest): ResponseEntity<CreateNotificationResponse> {
        val result = notificationService.createOrGetNotification(request)
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
            CreateNotificationResponse(result.notification.id, result.notification.status, result.duplicated)
        )
    }

    @GetMapping("/{notificationId}")
    fun get(@PathVariable notificationId: Long): ResponseEntity<NotificationDetailResponse> =
        ResponseEntity.ok(NotificationDetailResponse.from(notificationService.getNotification(notificationId)))

    @PatchMapping("/{notificationId}/read")
    fun read(
        @PathVariable notificationId: Long,
        @RequestHeader("X-USER-ID") userId: Long,
    ): ResponseEntity<Unit> {
        notificationService.markAsRead(notificationId, userId)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/{notificationId}/retry")
    fun retry(@PathVariable notificationId: Long): ResponseEntity<CreateNotificationResponse> {
        val n = notificationService.manualRetry(notificationId)
        return ResponseEntity.ok(CreateNotificationResponse(n.id, n.status, false))
    }
}