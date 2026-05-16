package me.uno.chatbotapi.application.service

import me.uno.chatbotapi.adapter.inbound.web.dto.*
import me.uno.chatbotapi.application.port.inbound.LoginUseCase
import me.uno.chatbotapi.application.port.inbound.SignupUseCase
import me.uno.chatbotapi.application.port.inbound.TokenRefreshUseCase
import me.uno.chatbotapi.application.port.outbound.LoadUserPort
import me.uno.chatbotapi.application.port.outbound.SaveUserPort
import me.uno.chatbotapi.config.security.JwtProperties
import me.uno.chatbotapi.config.security.JwtProvider
import me.uno.chatbotapi.domain.UserAccount
import me.uno.chatbotapi.domain.UserRole
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Transactional
@Service
class AuthService(
    private val loadUserPort: LoadUserPort,
    private val saveUserPort: SaveUserPort,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider,
    private val jwtProperties: JwtProperties,
) : SignupUseCase, LoginUseCase, TokenRefreshUseCase {

    override fun signup(request: SignupRequest): SignupResponse {
        loadUserPort.loadUserByEmail(request.email)?.let {
            throw IllegalArgumentException("Already registered email")
        }

        val userAccount = UserAccount(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            name = request.name,
            role = UserRole.MEMBER,
            createdAt = OffsetDateTime.now(),
        )

        val savedUser = saveUserPort.saveUser(userAccount)

        return SignupResponse(
            email = savedUser.email,
            name = savedUser.name,
            role = savedUser.role,
            createdAt = savedUser.createdAt,
        )
    }

    @Transactional(readOnly = true)
    override fun login(request: LoginRequest): LoginResponse {
        val user = loadUserPort.loadUserByEmail(request.email)
            ?: throw IllegalArgumentException("User not found")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw IllegalArgumentException("Invalid password")
        }

        return createLoginResponse(user)
    }

    @Transactional(readOnly = true)
    override fun refresh(request: RefreshRequest): LoginResponse {
        if (!jwtProvider.validateToken(request.refreshToken)) {
            throw IllegalArgumentException("Invalid refresh token")
        }

        val email = jwtProvider.getEmailFromToken(request.refreshToken)
        val user = loadUserPort.loadUserByEmail(email)
            ?: throw IllegalArgumentException("User not found")

        return createLoginResponse(user)
    }

    private fun createLoginResponse(userAccount: UserAccount): LoginResponse {
        val accessToken = jwtProvider.createAccessToken(userAccount.email, userAccount.role)
        val refreshToken = jwtProvider.createRefreshToken(userAccount.email, userAccount.role)

        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = jwtProperties.accessTokenExpiration.toSeconds(),
            refreshExpiresIn = jwtProperties.refreshTokenExpiration.toSeconds(),
        )
    }

}
