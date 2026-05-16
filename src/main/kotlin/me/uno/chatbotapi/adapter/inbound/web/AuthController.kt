package me.uno.chatbotapi.adapter.inbound.web

import me.uno.chatbotapi.adapter.inbound.web.dto.*
import me.uno.chatbotapi.application.port.inbound.LoginUseCase
import me.uno.chatbotapi.application.port.inbound.SignupUseCase
import me.uno.chatbotapi.application.port.inbound.TokenRefreshUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val signupUseCase: SignupUseCase,
    private val loginUseCase: LoginUseCase,
    private val tokenRefreshUseCase: TokenRefreshUseCase,
) {

    @PostMapping("/signup")
    fun signup(@RequestBody request: SignupRequest): ResponseEntity<SignupResponse> {
        return ResponseEntity.ok(signupUseCase.signup(request))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        return ResponseEntity.ok(loginUseCase.login(request))
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshRequest): ResponseEntity<LoginResponse> {
        return ResponseEntity.ok(tokenRefreshUseCase.refresh(request))
    }

}
