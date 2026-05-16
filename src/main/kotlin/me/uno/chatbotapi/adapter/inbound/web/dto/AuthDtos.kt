package me.uno.chatbotapi.adapter.inbound.web.dto

import me.uno.chatbotapi.domain.UserRole
import java.time.OffsetDateTime

data class SignupRequest(
    val email: String,
    val password: String,
    val name: String,
)

data class SignupResponse(
    val email: String,
    val name: String,
    val role: UserRole,
    val createdAt: OffsetDateTime,
)

data class LoginRequest(
    val email: String,
    val password: String,
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val refreshExpiresIn: Long,
)

data class RefreshRequest(
    val refreshToken: String,
)
