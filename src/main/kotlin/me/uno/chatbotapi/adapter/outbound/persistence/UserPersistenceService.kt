package me.uno.chatbotapi.adapter.outbound.persistence

import me.uno.chatbotapi.adapter.outbound.persistence.entity.UserAccountEntity
import me.uno.chatbotapi.adapter.outbound.persistence.repository.UserAccountJpaRepository
import me.uno.chatbotapi.application.port.outbound.LoadUserPort
import me.uno.chatbotapi.application.port.outbound.SaveUserPort
import me.uno.chatbotapi.domain.UserAccount
import org.springframework.stereotype.Service

@Service
class UserPersistenceService(
    private val userAccountJpaRepository: UserAccountJpaRepository,
) : LoadUserPort, SaveUserPort {

    override fun loadUserByEmail(email: String): UserAccount? {
        return userAccountJpaRepository.findByEmail(email)?.toDomain()
    }

    override fun saveUser(userAccount: UserAccount): UserAccount {
        val entity = userAccountJpaRepository.save(userAccount.toEntity())
        return entity.toDomain()
    }

    private fun UserAccountEntity.toDomain() = UserAccount(
        id = id,
        email = email,
        password = password,
        name = name,
        role = role,
        createdAt = createdAt,
    )

    private fun UserAccount.toEntity() = UserAccountEntity(
        email = email,
        password = password,
        name = name,
        role = role,
    )

}
