package de.livepoll.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class MailConfig {

    @Bean
    fun getMailer() = JavaMailSenderImpl().apply {
        host = System.getenv("LIVE_POLL_MAIL_HOST")
        port = System.getenv("LIVE_POLL_MAIL_PORT").toInt()
        username = System.getenv("LIVE_POLL_MAIL_USERNAME")
        password = System.getenv("LIVE_POLL_MAIL_PASSWORD")
        javaMailProperties.apply {
            put("mail.transport.protocol", "smtp")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.debug", "true")
        }
    }
}