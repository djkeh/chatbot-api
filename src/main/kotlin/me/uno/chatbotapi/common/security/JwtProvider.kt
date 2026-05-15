package me.uno.chatbotapi.common.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import me.uno.chatbotapi.domain.UserRole
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtProvider(
    @Value("\${jwt.secret}") private val secretKeyString: String,
    @Value("\${jwt.access-token-expiration-ms}") private val accessTokenExpirationMs: Long,
    @Value("\${jwt.refresh-token-expiration-ms}") private val refreshTokenExpirationMs: Long,
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(secretKeyString.toByteArray(StandardCharsets.UTF_8))

    fun createAccessToken(email: String, role: UserRole): String {
        return createToken(email, role, accessTokenExpirationMs)
    }

    fun createRefreshToken(email: String, role: UserRole): String {
        return createToken(email, role, refreshTokenExpirationMs)
    }

    private fun createToken(email: String, role: UserRole, expirationMs: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + expirationMs)

        return Jwts.builder()
            .subject(email)
            .claim("role", role.name)
            .issuedAt(now)
            .expiration(expiryDate)
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

