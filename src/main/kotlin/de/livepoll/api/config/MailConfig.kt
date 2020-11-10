package de.livepoll.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
class MailConfig {

    @Bean
    fun getMailer(): JavaMailSender? {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = System.getenv("LIVE_POLL_MAIL_HOST")
        mailSender.port = System.getenv("LIVE_POLL_MAIL_PORT").toInt()
        mailSender.username = System.getenv("LIVE_POLL_MAIL_USERNAME")
        mailSender.password = System.getenv("LIVE_POLL_MAIL_PASSWORD")
        val props: Properties = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.debug"] = "true"
        return mailSender
    }
}