package me.uno.chatbotapi.adapter.outbound.persistence

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

@EntityListeners(AuditingEntityListener::class)
@MappedSuperclass
abstract class AuditingFields {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: OffsetDateTime = DUMMY_DATETIME
        protected set

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: OffsetDateTime = DUMMY_DATETIME
        protected set

    override fun toString(): String {
        return "createdAt=$createdAt, updatedAt=$updatedAt"
    }

    companion object {
        val DUMMY_DATETIME: OffsetDateTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC).atOffset(ZoneOffset.UTC)
    }

}
