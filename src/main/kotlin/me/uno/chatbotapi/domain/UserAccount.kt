package me.uno.chatbotapi.domain

import java.time.OffsetDateTime

data class UserAccount(
    val id: Long = 0L,
    val email: String,
    val password: String,
    val name: String,
    val role: UserRole,
    val createdAt: OffsetDateTime,
)
