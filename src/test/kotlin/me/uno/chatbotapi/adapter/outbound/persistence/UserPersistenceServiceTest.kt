package me.uno.chatbotapi.adapter.outbound.persistence

import me.uno.chatbotapi.adapter.outbound.persistence.entity.UserAccountEntity
import me.uno.chatbotapi.adapter.outbound.persistence.repository.UserAccountJpaRepository
import me.uno.chatbotapi.config.JpaConfig
import me.uno.chatbotapi.domain.UserAccount
import me.uno.chatbotapi.domain.UserRole
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import java.time.OffsetDateTime

@DisplayName("[Repository] 사용자 저장소 테스트")
@Import(UserPersistenceService::class, JpaConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class UserPersistenceServiceTest @Autowired constructor(
    private val sut: UserPersistenceService,
    private val userAccountJpaRepository: UserAccountJpaRepository,
) {

    @Test
    fun `존재하는 이메일이 주어지면, 사용자 도메인 객체를 반환한다`() {
        // given
        val entity = UserAccountEntity(
            email = "test@example.com",
            password = "encodedPassword",
            name = "테스트 유저",
            role = UserRole.MEMBER,
        )
        userAccountJpaRepository.save(entity)

        // when
        val result = sut.loadUserByEmail("test@example.com")

        // then
        assertThat(result)
            .isNotNull
            .hasFieldOrPropertyWithValue("email", "test@example.com")
            .hasFieldOrPropertyWithValue("name", "테스트 유저")
            .hasFieldOrPropertyWithValue("role", UserRole.MEMBER)
    }

    @Test
    fun `존재하지 않는 이메일이 주어지면, null을 반환한다`() {
        // when
        val result = sut.loadUserByEmail("notexist@example.com")

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `사용자 도메인 객체가 주어지면, 저장 후 ID가 할당된 사용자를 반환한다`() {
        // given
        val userAccount = UserAccount(
            email = "new@example.com",
            password = "encodedPassword",
            name = "신규 유저",
            role = UserRole.MEMBER,
            createdAt = OffsetDateTime.now(),
        )

        // when
        val result = sut.saveUser(userAccount)

        // then
        assertThat(result.id).isGreaterThan(0L)
        assertThat(result)
            .hasFieldOrPropertyWithValue("email", "new@example.com")
            .hasFieldOrPropertyWithValue("name", "신규 유저")
            .hasFieldOrPropertyWithValue("role", UserRole.MEMBER)
        assertThat(result.createdAt).isAfter(AuditingFields.DUMMY_DATETIME)
    }

}
