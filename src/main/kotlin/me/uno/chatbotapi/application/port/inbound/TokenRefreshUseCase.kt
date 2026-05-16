package me.uno.chatbotapi.application.port.inbound

import me.uno.chatbotapi.adapter.inbound.web.dto.LoginResponse
import me.uno.chatbotapi.adapter.inbound.web.dto.RefreshRequest

interface TokenRefreshUseCase {
    fun refresh(request: RefreshRequest): LoginResponse
}
