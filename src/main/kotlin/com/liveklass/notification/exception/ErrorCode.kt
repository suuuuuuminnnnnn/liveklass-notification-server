package com.liveklass.notification.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(val status: HttpStatus, val code: String, val message: String) {
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND", "알림을 찾을 수 없습니다."),
    TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "TEMPLATE_NOT_FOUND", "활성 템플릿을 찾을 수 없습니다."),
    MISSING_TEMPLATE_VARIABLE(HttpStatus.BAD_REQUEST, "MISSING_TEMPLATE_VARIABLE", "필수 템플릿 변수가 누락되었습니다."),
    INVALID_NOTIFICATION_STATE(HttpStatus.CONFLICT, "INVALID_NOTIFICATION_STATE", "유효하지 않은 알림 상태입니다."),
    FORBIDDEN_NOTIFICATION_ACCESS(HttpStatus.NOT_FOUND, "NOT_FOUND", "알림에 접근할 수 없습니다."),
}
