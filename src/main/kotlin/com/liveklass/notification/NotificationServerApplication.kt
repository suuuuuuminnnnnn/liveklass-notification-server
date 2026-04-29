package com.liveklass.notification

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class NotificationServerApplication

fun main(args: Array<String>) {
	runApplication<NotificationServerApplication>(*args)
}
