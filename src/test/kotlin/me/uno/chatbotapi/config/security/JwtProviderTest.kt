@file:Suppress("NonAsciiCharacters")

package me.uno.chatbotapi.config.security

import io.mockk.every
import io.mockk.mockkStatic
import me.uno.chatbotapi.domain.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

@DisplayName("[Unit] JWT 구현체 테스트")
class JwtProviderTest {

    private val jwtProperties = JwtProperties(
        secret = "vms987654321vms987654321vms987654321vms987654321vms987654321",
        accessTokenExpiration = Duration.ofMinutes(5),
        refreshTokenExpiration = Duration.ofDays(1),
    )

    private val sut = JwtProvider(jwtProperties)

    @Test
    fun `이메일과 역할이 주어지면, 액세스 토큰을 반환한다`() = mockkStatic(Instant::class) {
        // given
        val email = "test@email.com"
        val role = UserRole.MEMBER
        every { Instant.now() } returns Instant.parse("2026-05-01T01:02:03Z")

        // when
        val result = sut.createAccessToken(email, role)

        // then
        assertThat(result)
            .isEqualTo("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0ZXN0QGVtYWlsLmNvbSIsInJvbGUiOiJNRU1CRVIiLCJpYXQiOjE3Nzc1OTczMjMsImV4cCI6MTc3NzU5NzYyM30.L3Xu7lMEioImUjOZEaagipoInSrekAt1qWsXjou4BOpexXcHlDQwzJlSsxFaxTra")
    }

    @Test
    fun `이메일과 역할이 주어지면, 리프레시 토큰을 반환한다`() = mockkStatic(Instant::class) {
        // given
        val email = "test@email.com"
        val role = UserRole.MEMBER
        every { Instant.now() } returns Instant.parse("2026-05-01T01:02:03Z")

        // when
        val result = sut.createRefreshToken(email, role)

        // then
        assertThat(result)
            .isEqualTo("eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0ZXN0QGVtYWlsLmNvbSIsInJvbGUiOiJNRU1CRVIiLCJpYXQiOjE3Nzc1OTczMjMsImV4cCI6MTc3NzY4MzcyM30.o8Y0aRtjUVplKaUhU7vxrLWlXH3JLxmhhwZt0Bvq5xvxX5-C_fndRCB3lrv3lrIP")
    }

    @Test
    fun `유효한 토큰이 주어지면, 이메일을 반환한다`() {
        // given
        val token = sut.createAccessToken("test@email.com", UserRole.MEMBER)

        // when
        val result = sut.getEmailFromToken(token)

        // then
        assertThat(result).isEqualTo("test@email.com")
    }

    @Test
    fun `유효한 토큰이 주어지면, 역할을 반환한다`() {
        // given
        val token = sut.createAccessToken("test@email.com", UserRole.MEMBER)

        // when
        val result = sut.getRoleFromToken(token)

        // then
        assertThat(result).isEqualTo(UserRole.MEMBER)
    }

    @Test
    fun `유효한 토큰이 주어지면, true를 반환한다`() {
        // given
        val token = sut.createAccessToken("test@email.com", UserRole.MEMBER)

        // when
        val result = sut.validateToken(token)

        // then
        assertThat(result).isTrue()
    }

    @Test
    fun `유효하지 않은 토큰이 주어지면, false를 반환한다`() {
        // given
        val invalidToken = "invalid.token.value"

        // when
        val result = sut.validateToken(invalidToken)

        // then
        assertThat(result).isFalse()
    }

}
