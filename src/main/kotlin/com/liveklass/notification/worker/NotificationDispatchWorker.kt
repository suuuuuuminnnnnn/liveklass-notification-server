package com.liveklass.notification.worker

import com.liveklass.notification.sender.NotificationSenderRouter
import com.liveklass.notification.service.NotificationDispatchService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class NotificationDispatchWorker(
    private val notificationDispatchService: NotificationDispatchService,
    private val senderRouter: NotificationSenderRouter,
    @Value("\${notification.worker.batch-size:10}") private val batchSize: Int,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val instanceId = UUID.randomUUID().toString()

    @Scheduled(fixedDelay = 3000)
    fun dispatch() {
        val claimed = notificationDispatchService.claimReadyBatch(batchSize, instanceId)
        if (claimed.isEmpty()) return
        log.info("[Worker] claimed {} notifications", claimed.size)
        claimed.forEach { notification ->
            try {
                senderRouter.send(notification)
                notificationDispatchService.markSent(notification.id)
            } catch (e: Exception) {
                notificationDispatchService.markFailed(notification.id, e.message ?: "Unknown error")
                log.warn("[Worker] failed notificationId={}", notification.id, e)
            }
        }
    }
}
