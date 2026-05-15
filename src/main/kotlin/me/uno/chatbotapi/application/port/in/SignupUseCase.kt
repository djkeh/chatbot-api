package me.uno.chatbotapi.application.port.`in`

import me.uno.chatbotapi.adapter.inbound.web.dto.SignupRequest
import me.uno.chatbotapi.adapter.inbound.web.dto.SignupResponse

interface SignupUseCase {
    fun signup(request: SignupRequest): SignupResponse
}

