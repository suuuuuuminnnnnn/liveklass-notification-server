package com.liveklass.notification.repository

import com.liveklass.notification.entity.NotificationTemplate
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationTemplateRepository : JpaRepository<NotificationTemplate, Long>
