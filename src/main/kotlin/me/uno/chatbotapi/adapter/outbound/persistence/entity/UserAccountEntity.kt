package me.uno.chatbotapi.adapter.outbound.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import me.uno.chatbotapi.adapter.outbound.persistence.AuditingFields
import me.uno.chatbotapi.domain.UserRole

@Table(name = "user_account")
@Entity
class UserAccountEntity(
    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole = UserRole.MEMBER,
) : AuditingFields() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

    override fun toString(): String {
        return "UserAccountEntity(" +
                "id=$id, " +
                "email='$email', " +
                "name='$name', " +
                "role=$role, " +
                super.toString() +
                ")"
    }
}
