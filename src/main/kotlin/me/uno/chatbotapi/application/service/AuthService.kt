package me.uno.chatbotapi.application.service

import me.uno.chatbotapi.adapter.inbound.web.dto.*
import me.uno.chatbotapi.application.port.`in`.LoginUseCase
import me.uno.chatbotapi.application.port.`in`.SignupUseCase
import me.uno.chatbotapi.application.port.`in`.TokenRefreshUseCase
import me.uno.chatbotapi.application.port.out.LoadUserPort
import me.uno.chatbotapi.application.port.out.SaveUserPort
import me.uno.chatbotapi.common.security.JwtProvider
import me.uno.chatbotapi.domain.User
import me.uno.chatbotapi.domain.UserRole
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

@Service
@Transactional
class AuthService(
    private val loadUserPort: LoadUserPort,
    private val saveUserPort: SaveUserPort,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider,
    @Value("\${jwt.access-token-expiration-ms}") private val accessTokenExpirationMs: Long = 300_000,
    @Value("\${jwt.refresh-token-expiration-ms}") private val refreshTokenExpirationMs: Long = 86_400_000,
) : SignupUseCase, LoginUseCase, TokenRefreshUseCase {

    override fun signup(request: SignupRequest): SignupResponse {
        loadUserPort.loadUserByEmail(request.email)?.let {
            throw IllegalArgumentException("Already registered email")
        }

        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            name = request.name,
            role = UserRole.MEMBER,
            createdAt = OffsetDateTime.now(),
        )

        val savedUser = saveUserPort.saveUser(user)

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

    override fun refresh(request: RefreshRequest): LoginResponse {
        if (!jwtProvider.validateToken(request.refreshToken)) {
            throw IllegalArgumentException("Invalid refresh token")
        }

        val email = jwtProvider.getEmailFromToken(request.refreshToken)
        val user = loadUserPort.loadUserByEmail(email)
            ?: throw IllegalArgumentException("User not found")

        return createLoginResponse(user)
    }

    private fun createLoginResponse(user: User): LoginResponse {
        val accessToken = jwtProvider.createAccessToken(user.email, user.role)
        val refreshToken = jwtProvider.createRefreshToken(user.email, user.role)

        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = accessTokenExpirationMs / 1000,
            refreshExpiresIn = refreshTokenExpirationMs / 1000,
        )
    }
}
