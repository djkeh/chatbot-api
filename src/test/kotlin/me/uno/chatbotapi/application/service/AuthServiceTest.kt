package me.uno.chatbotapi.application.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.uno.chatbotapi.adapter.inbound.web.dto.SignupRequest
import me.uno.chatbotapi.application.port.outbound.LoadUserPort
import me.uno.chatbotapi.application.port.outbound.SaveUserPort
import me.uno.chatbotapi.config.security.JwtProperties
import me.uno.chatbotapi.config.security.JwtProvider
import me.uno.chatbotapi.domain.UserAccount
import me.uno.chatbotapi.domain.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Duration
import java.time.OffsetDateTime

@DisplayName("[Unit] AuthService Test")
class AuthServiceTest {

    private val loadUserPort = mockk<LoadUserPort>()
    private val saveUserPort = mockk<SaveUserPort>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val jwtProvider = mockk<JwtProvider>()
    private val jwtProperties = JwtProperties(
        secret = "vms987654321vms987654321vms987654321vms987654321vms987654321",
        accessTokenExpiration = Duration.ofMinutes(5),
        refreshTokenExpiration = Duration.ofDays(1)
    )

    private val sut = AuthService(
        loadUserPort = loadUserPort,
        saveUserPort = saveUserPort,
        passwordEncoder = passwordEncoder,
        jwtProvider = jwtProvider,
        jwtProperties = jwtProperties
    )

    @Test
    fun `회원가입 정보가 주어지면, 회원을 저장하고 응답을 반환한다`() {
        // Given
        val request = SignupRequest("test@example.com", "password", "Tester")
        every { loadUserPort.loadUserByEmail(request.email) } returns null
        every { passwordEncoder.encode(request.password) } returns "encoded-password"
        every { saveUserPort.saveUser(any()) } answers {
            val user = it.invocation.args[0] as UserAccount
            user.copy(id = 1L)
        }

        // When
        val result = sut.signup(request)

        // Then
        assertThat(result.email).isEqualTo(request.email)
        assertThat(result.name).isEqualTo(request.name)
        assertThat(result.role).isEqualTo(UserRole.MEMBER)
        verify { saveUserPort.saveUser(any()) }
    }

    @Test
    fun `이미 존재하는 이메일로 회원가입을 요청하면, 예외가 발생한다`() {
        // Given
        val request = SignupRequest("test@example.com", "password", "Tester")
        val existingUser = UserAccount(
            email = request.email,
            password = "old-password",
            name = "Existing",
            role = UserRole.MEMBER,
            createdAt = OffsetDateTime.now()
        )
        every { loadUserPort.loadUserByEmail(request.email) } returns existingUser

        // When & Then
        assertThatThrownBy { sut.signup(request) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Already registered email")
    }

}
