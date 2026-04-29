package com.liveklass.notification.service

import com.liveklass.notification.entity.Notification
import com.liveklass.notification.exception.ErrorCode
import com.liveklass.notification.exception.GlobalException
import com.liveklass.notification.repository.NotificationRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class NotificationDispatchService(
    private val notificationRepository: NotificationRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun claimReadyBatch(limit: Int, instanceId: String): List<Notification> {
        val batch = notificationRepository.findReadyNotificationsWithLock(limit)
        val now = LocalDateTime.now()
        batch.forEach { it.claim(instanceId, now) }
        return notificationRepository.saveAll(batch)
    }

    @Transactional
    fun markSent(id: Long) {
        val n = notificationRepository.findById(id)
            .orElseThrow { GlobalException(ErrorCode.NOTIFICATION_NOT_FOUND, "알림을 찾을 수 없습니다. id=$id") }
        n.markSent(LocalDateTime.now())
        notificationRepository.save(n)
    }

    @Transactional
    fun markFailed(id: Long, reason: String) {
        val n = notificationRepository.findById(id)
            .orElseThrow { GlobalException(ErrorCode.NOTIFICATION_NOT_FOUND, "알림을 찾을 수 없습니다. id=$id") }
        n.handleFailure(reason, LocalDateTime.now())
        notificationRepository.save(n)
    }

    @Transactional
    fun recoverStuckNotifications(stuckThresholdMinutes: Long) {
        val threshold = LocalDateTime.now().minusMinutes(stuckThresholdMinutes)
        val stuck = notificationRepository.findStuckProcessingNotifications(threshold)
        if (stuck.isEmpty()) return
        val now = LocalDateTime.now()
        stuck.forEach { it.recoverFromStuck(now) }
        notificationRepository.saveAll(stuck)
        log.info("[Recovery] recovered {} stuck notifications", stuck.size)
    }
}