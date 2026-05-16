package me.uno.chatbotapi.application.port.outbound

import me.uno.chatbotapi.domain.UserAccount

interface LoadUserPort {
    fun loadUserByEmail(email: String): UserAccount?
}
