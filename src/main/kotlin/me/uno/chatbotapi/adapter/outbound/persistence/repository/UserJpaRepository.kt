package me.uno.chatbotapi.adapter.outbound.persistence.repository

import me.uno.chatbotapi.adapter.outbound.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity?
}

