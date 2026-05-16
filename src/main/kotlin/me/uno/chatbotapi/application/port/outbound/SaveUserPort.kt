package me.uno.chatbotapi.application.port.outbound

import me.uno.chatbotapi.domain.UserAccount

interface SaveUserPort {
    fun saveUser(userAccount: UserAccount): UserAccount
}
