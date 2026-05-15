package me.uno.chatbotapi.adapter.outbound.persistence.entity

import jakarta.persistence.*
import me.uno.chatbotapi.domain.UserRole
import java.time.OffsetDateTime

@Entity
@Table(name = "users")
class UserEntity(
    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(nullable = false)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: UserRole,

    @Column(nullable = false, updatable = false)
    val createdAt: OffsetDateTime,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    override fun toString(): String {
        return "UserEntity(" +
                "id=$id, " +
                "email='$email', " +
                "name='$name', " +
                "role=$role, " +
                "createdAt=$createdAt" +
                ")"
    }
}

