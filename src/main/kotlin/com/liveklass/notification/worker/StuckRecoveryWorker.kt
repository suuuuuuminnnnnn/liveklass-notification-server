package com.liveklass.notification.worker

import com.liveklass.notification.service.NotificationDispatchService
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class StuckRecoveryWorker(
    private val notificationDispatchService: NotificationDispatchService,
    @Value("\${notification.worker.stuck-threshold-minutes:5}") private val threshold: Long,
) {
    @Scheduled(fixedDelay = 60000)
    fun recover() = notificationDispatchService.recoverStuckNotifications(threshold)
}
