package me.uno.chatbotapi.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.time.OffsetDateTime
import java.util.Optional

@EnableJpaAuditing(dateTimeProviderRef = "offSetDateTimeProvider")
@Configuration
class JpaConfig {

    @Bean
    fun offSetDateTimeProvider(): DateTimeProvider {
        return DateTimeProvider { Optional.of(OffsetDateTime.now()) }
    }

}
