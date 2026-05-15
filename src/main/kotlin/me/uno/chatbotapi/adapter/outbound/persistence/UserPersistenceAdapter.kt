package me.uno.chatbotapi.adapter.outbound.persistence

import me.uno.chatbotapi.adapter.outbound.persistence.entity.UserEntity
import me.uno.chatbotapi.adapter.outbound.persistence.repository.UserJpaRepository
import me.uno.chatbotapi.application.port.out.LoadUserPort
import me.uno.chatbotapi.application.port.out.SaveUserPort
import me.uno.chatbotapi.domain.User
import org.springframework.stereotype.Component

@Component
class UserPersistenceAdapter(
    private val userJpaRepository: UserJpaRepository,
) : LoadUserPort, SaveUserPort {

    override fun loadUserByEmail(email: String): User? {
        return userJpaRepository.findByEmail(email)?.toDomain()
    }

    override fun saveUser(user: User): User {
        val entity = userJpaRepository.save(user.toEntity())
        return entity.toDomain()
    }

    private fun UserEntity.toDomain() = User(
        id = id,
        email = email,
        password = password,
        name = name,
        role = role,
        createdAt = createdAt,
    )

    private fun User.toEntity() = UserEntity(
        email = email,
        password = password,
        name = name,
        role = role,
        createdAt = createdAt,
    )
}


