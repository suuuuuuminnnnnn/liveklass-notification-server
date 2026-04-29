package com.liveklass.notification.service

import com.liveklass.notification.dto.CreateNotificationRequest
import com.liveklass.notification.entity.Notification
import com.liveklass.notification.enums.NotificationStatus
import com.liveklass.notification.enums.ReadStatus
import com.liveklass.notification.exception.ErrorCode
import com.liveklass.notification.exception.GlobalException
import com.liveklass.notification.repository.NotificationRepository
import com.liveklass.notification.repository.NotificationTemplateRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val notificationTemplateRepository: NotificationTemplateRepository,
    private val notificationSaveTransactionHelper: NotificationSaveTransactionHelper,
    private val templateRenderer: TemplateRenderer,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    data class CreateResult(val notification: Notification, val duplicated: Boolean)

    fun createOrGetNotification(request: CreateNotificationRequest): CreateResult {
        val key = buildDeduplicationKey(request)

        notificationRepository.findByDeduplicationKey(key)?.let {
            return CreateResult(it, duplicated = true)
        }

        val template = notificationTemplateRepository
            .findByNotificationTypeAndChannelAndEnabledTrue(request.type, request.channel)
            ?: throw GlobalException(ErrorCode.TEMPLATE_NOT_FOUND, "활성 템플릿을 찾을 수 없습니다. type=${request.type}, channel=${request.channel}")

        val title = templateRenderer.render(template.titleTemplate, request.templateVariables)
        val content = templateRenderer.render(template.contentTemplate, request.templateVariables)

        val now = LocalDateTime.now()
        val notification = Notification(
            recipientId = request.recipientId,
            notificationType = request.type,
            channel = request.channel,
            referenceType = request.referenceType,
            referenceId = request.referenceId,
            deduplicationKey = key,
            title = title,
            content = content,
            scheduledAt = request.scheduledAt ?: now,
            nextRetryAt = request.scheduledAt ?: now,
        )

        return try {
            val saved = notificationSaveTransactionHelper.saveAndFlush(notification)
            CreateResult(saved, duplicated = false)
        } catch (e: DataIntegrityViolationException) {
            val existing = notificationRepository.findByDeduplicationKey(key)!!
            CreateResult(existing, duplicated = true)
        }
    }

    fun getNotification(id: Long): Notification =
        notificationRepository.findById(id)
            .orElseThrow { GlobalException(ErrorCode.NOTIFICATION_NOT_FOUND, "알림을 찾을 수 없습니다. id=$id") }

    fun getUserNotifications(recipientId: Long, readStatus: ReadStatus?): List<Notification> =
        if (readStatus != null)
            notificationRepository.findByRecipientIdAndReadStatusOrderByCreatedAtDesc(recipientId, readStatus)
        else
            notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId)

    @Transactional
    fun markAsRead(notificationId: Long, userId: Long) {
        val n = notificationRepository.findById(notificationId)
            .orElseThrow { GlobalException(ErrorCode.NOTIFICATION_NOT_FOUND, "알림을 찾을 수 없습니다. id=$notificationId") }
        if (n.recipientId != userId)
            throw GlobalException(ErrorCode.FORBIDDEN_NOTIFICATION_ACCESS, "알림에 접근할 수 없습니다. id=$notificationId")
        n.markAsRead(LocalDateTime.now())
        notificationRepository.save(n)
    }

    @Transactional
    fun manualRetry(notificationId: Long): Notification {
        val n = notificationRepository.findById(notificationId)
            .orElseThrow { GlobalException(ErrorCode.NOTIFICATION_NOT_FOUND, "알림을 찾을 수 없습니다. id=$notificationId") }
        if (n.status != NotificationStatus.FAILED)
            throw GlobalException(ErrorCode.INVALID_NOTIFICATION_STATE, "수동 재시도는 FAILED 상태에서만 가능합니다. current=${n.status}")
        n.prepareManualRetry(LocalDateTime.now())
        return notificationRepository.save(n)
    }

    private fun buildDeduplicationKey(r: CreateNotificationRequest): String =
        "${r.recipientId}:${r.type}:${r.channel}:${r.referenceType}:${r.referenceId}"
}
