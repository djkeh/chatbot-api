package me.uno.chatbotapi.adapter.inbound.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import me.uno.chatbotapi.adapter.inbound.web.dto.LoginRequest
import me.uno.chatbotapi.adapter.inbound.web.dto.LoginResponse
import me.uno.chatbotapi.adapter.inbound.web.dto.RefreshRequest
import me.uno.chatbotapi.adapter.inbound.web.dto.SignupRequest
import me.uno.chatbotapi.adapter.inbound.web.dto.SignupResponse
import me.uno.chatbotapi.application.port.inbound.LoginUseCase
import me.uno.chatbotapi.application.port.inbound.SignupUseCase
import me.uno.chatbotapi.application.port.inbound.TokenRefreshUseCase
import me.uno.chatbotapi.config.security.JwtProvider
import me.uno.chatbotapi.domain.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.assertj.MockMvcTester
import java.time.OffsetDateTime

@DisplayName("[Controller] 인증 컨트롤러 테스트")
@AutoConfigureMockMvc(addFilters = false) // JWT 필터 제외하고 테스트
@WebMvcTest(AuthController::class)
class AuthControllerTest @Autowired constructor(
    private val mvc: MockMvcTester,
    private val objectMapper: ObjectMapper,
    @MockkBean private val signupUseCase: SignupUseCase,
    @MockkBean private val loginUseCase: LoginUseCase,
    @MockkBean private val tokenRefreshUseCase: TokenRefreshUseCase,
    @MockkBean private val jwtProvider: JwtProvider,
) {

    @Test
    fun `회원가입을 요청하면, 성공 응답을 반환한다`() {
        // Given
        val request = SignupRequest("test@example.com", "password", "Tester")
        val response = SignupResponse("test@example.com", "Tester", UserRole.MEMBER, OffsetDateTime.now())
        every { signupUseCase.signup(any()) } returns response

        // When & Then
        assertThat(
            mvc.post()
                .uri("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(
                """
                {
                    "email": "${response.email}",
                    "name": "${response.name}",
                    "role": "${UserRole.MEMBER}"
                }
                """.trimIndent()
            )
        verify { signupUseCase.signup(any()) }
    }

    @Test
    fun `로그인을 요청하면, 토큰 정보를 반환한다`() {
        // Given
        val request = LoginRequest("test@example.com", "password")
        val response = LoginResponse("access", "refresh", "Bearer", 300, 86400)
        every { loginUseCase.login(any()) } returns response

        // When & Then
        assertThat(
            mvc.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo(
                """
                {
                    "accessToken": "access",
                    "refreshToken": "refresh"
                }
                """.trimIndent()
            )
        verify { loginUseCase.login(any()) }
    }

    @Test
    fun `토큰 갱신을 요청하면, 새로운 토큰 정보를 반환한다`() {
        // Given
        val request = RefreshRequest("refresh-token")
        val response = LoginResponse("new-access", "new-refresh", "Bearer", 300, 86400)
        every { tokenRefreshUseCase.refresh(any()) } returns response

        // When & Then
        assertThat(
            mvc.post()
                .uri("/api/v1/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .hasStatusOk()
            .bodyJson()
            .isLenientlyEqualTo("""
                {
                    "accessToken": "new-access",
                    "refreshToken": "new-refresh"
                }
            """)
        verify { tokenRefreshUseCase.refresh(any()) }
    }

}
