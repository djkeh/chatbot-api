package me.uno.chatbotapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class ChatbotApiApplication

fun main(args: Array<String>) {
    runApplication<ChatbotApiApplication>(*args)
}
