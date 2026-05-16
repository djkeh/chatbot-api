package me.uno.chatbotapi.application.port.inbound

import me.uno.chatbotapi.adapter.inbound.web.dto.LoginRequest
import me.uno.chatbotapi.adapter.inbound.web.dto.LoginResponse

interface LoginUseCase {
    fun login(request: LoginRequest): LoginResponse
}
