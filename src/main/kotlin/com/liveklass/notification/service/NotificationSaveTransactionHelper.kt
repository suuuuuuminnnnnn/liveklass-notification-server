package com.liveklass.notification.service

import com.liveklass.notification.entity.Notification
import com.liveklass.notification.repository.NotificationRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class NotificationSaveTransactionHelper(
    private val notificationRepository: NotificationRepository,
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveAndFlush(notification: Notification): Notification =
        notificationRepository.saveAndFlush(notification)
}
