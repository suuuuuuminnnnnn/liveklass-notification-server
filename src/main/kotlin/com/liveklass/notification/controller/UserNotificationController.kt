package com.liveklass.notification.controller

import com.liveklass.notification.dto.UserNotificationResponse
import com.liveklass.notification.enums.ReadStatus
import com.liveklass.notification.service.NotificationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserNotificationController(
    private val notificationService: NotificationService,
) {
    @GetMapping("/{recipientId}/notifications")
    fun list(
        @PathVariable recipientId: Long,
        @RequestParam(required = false) readStatus: ReadStatus?,
    ): ResponseEntity<List<UserNotificationResponse>> =
        ResponseEntity.ok(
            notificationService.getUserNotifications(recipientId, readStatus)
                .map { UserNotificationResponse.from(it) }
        )
}