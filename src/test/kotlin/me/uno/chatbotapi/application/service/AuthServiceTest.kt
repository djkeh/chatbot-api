package me.uno.chatbotapi.application.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.uno.chatbotapi.adapter.inbound.web.dto.SignupRequest
import me.uno.chatbotapi.application.port.out.LoadUserPort
import me.uno.chatbotapi.application.port.out.SaveUserPort
import me.uno.chatbotapi.domain.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder

@DisplayName("[Unit] AuthService Test")
class AuthServiceTest {

    private val loadUserPort = mockk<LoadUserPort>()
    private val saveUserPort = mockk<SaveUserPort>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val jwtProvider = mockk<me.uno.chatbotapi.common.security.JwtProvider>()

    private val sut = AuthService(
        loadUserPort = loadUserPort,
        saveUserPort = saveUserPort,
        passwordEncoder = passwordEncoder,
        jwtProvider = jwtProvider,
    )

    @Test
    fun `회원가입 정보가 주어지면, 회원을 저장하고 응답을 반환한다`() {
        // Given
        val request = SignupRequest("test@example.com", "password", "Tester")
        every { loadUserPort.loadUserByEmail(request.email) } returns null
        every { passwordEncoder.encode(request.password) } returns "encoded-password"
        every { saveUserPort.saveUser(any()) } answers { it.invocation.args[0] as me.uno.chatbotapi.domain.User }

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
        every { loadUserPort.loadUserByEmail(request.email) } returns mockk()

        // When & Then
        assertThatThrownBy { sut.signup(request) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Already registered email")
    }
}
