package com.liveklass.notification

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NotificationServerApplication

fun main(args: Array<String>) {
	runApplication<NotificationServerApplication>(*args)
}
