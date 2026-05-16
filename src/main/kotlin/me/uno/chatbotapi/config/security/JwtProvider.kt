package me.uno.chatbotapi.config.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import me.uno.chatbotapi.domain.UserRole
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtProvider(
    private val jwtProperties: JwtProperties,
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray(StandardCharsets.UTF_8))

    fun createAccessToken(email: String, role: UserRole): String {
        return createToken(email, role, jwtProperties.accessTokenExpiration)
    }

    fun createRefreshToken(email: String, role: UserRole): String {
        return createToken(email, role, jwtProperties.refreshTokenExpiration)
    }

    private fun createToken(email: String, role: UserRole, tokenExpiration: Duration): String {
        val now = Instant.now()
        val expiresAt = now.plus(tokenExpiration)

        return Jwts.builder()
            .subject(email)
            .claim("role", role.name)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiresAt))
            .signWith(key)
            .compact()
    }

    fun getEmailFromToken(token: String): String {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
            .subject
    }

    fun getRoleFromToken(token: String): UserRole {
        val roleString = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload["role"] as String
        return UserRole.valueOf(roleString)
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

}
