package me.uno.chatbotapi.adapter.outbound.persistence.repository

import me.uno.chatbotapi.adapter.outbound.persistence.entity.UserAccountEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserAccountJpaRepository : JpaRepository<UserAccountEntity, Long> {
    fun findByEmail(email: String): UserAccountEntity?
}
