package me.uno.chatbotapi.domain

import java.time.OffsetDateTime

data class User(
    val id: Long? = null,
    val email: String,
    val password: String,
    val name: String,
    val role: UserRole,
    val createdAt: OffsetDateTime,
)


