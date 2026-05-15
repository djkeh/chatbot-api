package me.uno.chatbotapi.adapter.inbound.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import me.uno.chatbotapi.adapter.inbound.web.dto.*
import me.uno.chatbotapi.application.port.`in`.LoginUseCase
import me.uno.chatbotapi.application.port.`in`.SignupUseCase
import me.uno.chatbotapi.application.port.`in`.TokenRefreshUseCase
import me.uno.chatbotapi.common.security.JwtAuthenticationFilter
import me.uno.chatbotapi.common.security.JwtProvider
import me.uno.chatbotapi.domain.UserRole
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.OffsetDateTime

@WebMvcTest(AuthController::class)
@AutoConfigureMockMvc(addFilters = false) // JWT 필터 제외하고 테스트
@DisplayName("[WebMvc] AuthController Test")
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var signupUseCase: SignupUseCase

    @MockkBean
    private lateinit var loginUseCase: LoginUseCase

    @MockkBean
    private lateinit var tokenRefreshUseCase: TokenRefreshUseCase

    @MockkBean
    private lateinit var jwtProvider: JwtProvider // AuthConfig 등에서 필요할 수 있음

    @Test
    fun `회원가입을 요청하면, 성공 응답을 반환한다`() {
        // Given
        val request = SignupRequest("test@example.com", "password", "Tester")
        val response = SignupResponse("test@example.com", "Tester", UserRole.MEMBER, OffsetDateTime.now())
        every { signupUseCase.signup(any()) } returns response

        // When & Then
        mockMvc.perform(
            post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.email").value(response.email))
            .andExpect(jsonPath("$.name").value(response.name))
            .andExpect(jsonPath("$.role").value("MEMBER"))
    }

    @Test
    fun `로그인을 요청하면, 토큰 정보를 반환한다`() {
        // Given
        val request = LoginRequest("test@example.com", "password")
        val response = LoginResponse("access", "refresh", "Bearer", 300, 86400)
        every { loginUseCase.login(any()) } returns response

        // When & Then
        mockMvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").value("access"))
            .andExpect(jsonPath("$.refreshToken").value("refresh"))
    }
}


