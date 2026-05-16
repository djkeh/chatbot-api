package me.uno.chatbotapi.application.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import me.uno.chatbotapi.adapter.inbound.web.dto.LoginRequest
import me.uno.chatbotapi.adapter.inbound.web.dto.RefreshRequest
import me.uno.chatbotapi.adapter.inbound.web.dto.SignupRequest
import me.uno.chatbotapi.application.port.outbound.LoadUserPort
import me.uno.chatbotapi.application.port.outbound.SaveUserPort
import me.uno.chatbotapi.config.security.JwtProperties
import me.uno.chatbotapi.config.security.JwtProvider
import me.uno.chatbotapi.domain.UserAccount
import me.uno.chatbotapi.domain.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Duration
import java.time.OffsetDateTime

@DisplayName("[Service] 인증 서비스 테스트")
@ExtendWith(MockKExtension::class)
class AuthServiceTest(
    @MockK private val loadUserPort: LoadUserPort,
    @MockK private val saveUserPort: SaveUserPort,
    @MockK private val passwordEncoder: PasswordEncoder,
    @MockK private val jwtProvider: JwtProvider,
) {

    @SpyK
    private var jwtProperties: JwtProperties = JwtProperties(
        secret = "vms987654321vms987654321vms987654321vms987654321vms987654321",
        accessTokenExpiration = Duration.ofMinutes(5),
        refreshTokenExpiration = Duration.ofDays(1),
    )

    @InjectMockKs
    private lateinit var sut: AuthService

    @Test
    fun `회원가입 정보가 주어지면, 회원을 저장하고 응답을 반환한다`() {
        // given
        val request = SignupRequest("test@example.com", "password", "Tester")
        every { loadUserPort.loadUserByEmail(request.email) } returns null
        every { passwordEncoder.encode(request.password) } returns "encoded-password"
        every { saveUserPort.saveUser(any()) } answers {
            val user = it.invocation.args[0] as UserAccount
            user.copy(id = 1L)
        }

        // when
        val result = sut.signup(request)

        // then
        assertThat(result)
            .hasFieldOrPropertyWithValue("email", request.email)
            .hasFieldOrPropertyWithValue("name", request.name)
            .hasFieldOrPropertyWithValue("role", UserRole.MEMBER)
        verify { saveUserPort.saveUser(any()) }
    }

    @Test
    fun `이미 존재하는 이메일로 회원가입을 요청하면, 예외가 발생한다`() {
        // given
        val request = SignupRequest("test@example.com", "password", "Tester")
        val existingUser = UserAccount(
            email = request.email,
            password = "old-password",
            name = "Existing",
            role = UserRole.MEMBER,
            createdAt = OffsetDateTime.now(),
        )
        every { loadUserPort.loadUserByEmail(request.email) } returns existingUser

        // when
        val t = catchThrowable { sut.signup(request) }

        // then
        assertThat(t)
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Already registered email")
    }

    @Test
    fun `올바른 이메일과 비밀번호가 주어지면, 토큰을 반환한다`() {
        // given
        val request = LoginRequest("test@example.com", "password")
        val user = createUserAccount(email = request.email)
        every { loadUserPort.loadUserByEmail(request.email) } returns user
        every { passwordEncoder.matches(request.password, user.password) } returns true
        every { jwtProvider.createAccessToken(user.email, user.role) } returns "access-token"
        every { jwtProvider.createRefreshToken(user.email, user.role) } returns "refresh-token"

        // when
        val result = sut.login(request)

        // then
        assertThat(result)
            .hasFieldOrPropertyWithValue("accessToken", "access-token")
            .hasFieldOrPropertyWithValue("refreshToken", "refresh-token")
        verify { loadUserPort.loadUserByEmail(request.email) }
        verify { passwordEncoder.matches(request.password, user.password) }
    }

    @Test
    fun `존재하지 않는 이메일이 주어지면, IllegalArgumentException을 반환한다`() {
        // given
        val request = LoginRequest("notexist@example.com", "password")
        every { loadUserPort.loadUserByEmail(request.email) } returns null

        // when
        val t = catchThrowable { sut.login(request) }

        // then
        assertThat(t)
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("User not found")
    }

    @Test
    fun `잘못된 비밀번호가 주어지면, IllegalArgumentException을 반환한다`() {
        // given
        val request = LoginRequest("test@example.com", "wrong-password")
        val user = createUserAccount(email = request.email)
        every { loadUserPort.loadUserByEmail(request.email) } returns user
        every { passwordEncoder.matches(request.password, user.password) } returns false

        // when
        val t = catchThrowable { sut.login(request) }

        // then
        assertThat(t)
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Invalid password")
    }

    @Test
    fun `유효한 리프레시 토큰이 주어지면, 새 토큰을 반환한다`() {
        // given
        val request = RefreshRequest("valid-refresh-token")
        val user = createUserAccount()
        every { jwtProvider.validateToken(request.refreshToken) } returns true
        every { jwtProvider.getEmailFromToken(request.refreshToken) } returns user.email
        every { loadUserPort.loadUserByEmail(user.email) } returns user
        every { jwtProvider.createAccessToken(user.email, user.role) } returns "new-access-token"
        every { jwtProvider.createRefreshToken(user.email, user.role) } returns "new-refresh-token"

        // when
        val result = sut.refresh(request)

        // then
        assertThat(result)
            .hasFieldOrPropertyWithValue("accessToken", "new-access-token")
            .hasFieldOrPropertyWithValue("refreshToken", "new-refresh-token")
        verify { jwtProvider.validateToken(request.refreshToken) }
        verify { loadUserPort.loadUserByEmail(user.email) }
    }

    @Test
    fun `유효하지 않은 리프레시 토큰이 주어지면, IllegalArgumentException을 반환한다`() {
        // given
        val request = RefreshRequest("invalid-refresh-token")
        every { jwtProvider.validateToken(request.refreshToken) } returns false

        // when
        val t = catchThrowable { sut.refresh(request) }

        // then
        assertThat(t)
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Invalid refresh token")
    }

    @Test
    fun `유효한 리프레시 토큰이지만 존재하지 않는 사용자이면, IllegalArgumentException을 반환한다`() {
        // given
        val request = RefreshRequest("valid-refresh-token")
        every { jwtProvider.validateToken(request.refreshToken) } returns true
        every { jwtProvider.getEmailFromToken(request.refreshToken) } returns "ghost@example.com"
        every { loadUserPort.loadUserByEmail("ghost@example.com") } returns null

        // when
        val t = catchThrowable { sut.refresh(request) }

        // then
        assertThat(t)
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("User not found")
    }

    // ==================== fixtures ====================

    private fun createUserAccount(
        email: String = "test@example.com",
        password: String = "encoded-password",
        name: String = "Tester",
    ) = UserAccount(
        email = email,
        password = password,
        name = name,
        role = UserRole.MEMBER,
        createdAt = OffsetDateTime.now(),
    )

}
