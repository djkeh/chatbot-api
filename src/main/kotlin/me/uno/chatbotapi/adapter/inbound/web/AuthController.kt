package me.uno.chatbotapi.adapter.inbound.web

import me.uno.chatbotapi.adapter.inbound.web.dto.LoginRequest
import me.uno.chatbotapi.adapter.inbound.web.dto.LoginResponse
import me.uno.chatbotapi.adapter.inbound.web.dto.RefreshRequest
import me.uno.chatbotapi.adapter.inbound.web.dto.SignupRequest
import me.uno.chatbotapi.adapter.inbound.web.dto.SignupResponse
import me.uno.chatbotapi.application.port.inbound.LoginUseCase
import me.uno.chatbotapi.application.port.inbound.SignupUseCase
import me.uno.chatbotapi.application.port.inbound.TokenRefreshUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val signupUseCase: SignupUseCase,
    private val loginUseCase: LoginUseCase,
    private val tokenRefreshUseCase: TokenRefreshUseCase,
) {

    @PostMapping("/api/v1/auth/signup")
    fun signup(@RequestBody request: SignupRequest): SignupResponse {
        return signupUseCase.signup(request)
    }

    @PostMapping("/api/v1/auth/login")
    fun login(@RequestBody request: LoginRequest): LoginResponse {
        return loginUseCase.login(request)
    }

    @PostMapping("/api/v1/auth/refresh")
    fun refresh(@RequestBody request: RefreshRequest): LoginResponse {
        return tokenRefreshUseCase.refresh(request)
    }

}
