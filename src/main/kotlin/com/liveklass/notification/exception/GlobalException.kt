package com.liveklass.notification.exception

class GlobalException(
    val errorCode: ErrorCode,
    message: String = errorCode.message,
) : RuntimeException(message)
