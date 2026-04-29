package com.liveklass.notification.service

import com.liveklass.notification.exception.ErrorCode
import com.liveklass.notification.exception.GlobalException
import org.springframework.stereotype.Component

@Component
class TemplateRenderer {

    private val placeholderRegex = Regex("""\{(\w+)\}""")

    fun render(template: String, variables: Map<String, String>): String {
        val missing = extractPlaceholders(template) - variables.keys
        if (missing.isNotEmpty()) throw GlobalException(
            ErrorCode.MISSING_TEMPLATE_VARIABLE,
            "필수 템플릿 변수가 누락되었습니다: $missing"
        )
        return placeholderRegex.replace(template) { variables[it.groupValues[1]] ?: it.value }
    }

    private fun extractPlaceholders(template: String): Set<String> =
        placeholderRegex.findAll(template).map { it.groupValues[1] }.toSet()
}
