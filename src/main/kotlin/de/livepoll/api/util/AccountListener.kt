package de.livepoll.api.util

import de.livepoll.api.service.AccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class AccountListener: ApplicationListener<OnCreateAccountEvent> {

    private val serverUrl = System.getenv("LIVE_POLL_SERVER_URL")

    @Autowired
    private lateinit var accountService: AccountService

    @Autowired
    private lateinit var javaMailSender: JavaMailSender

    override fun onApplicationEvent(event: OnCreateAccountEvent) {
        this.confirmCreateAccount(event)
    }

    private fun confirmCreateAccount(event: OnCreateAccountEvent){
        val user = event.user
        val token = UUID.randomUUID().toString()
        accountService.createVerificationToken(user, token)

        val recipientAddress = user.email
        val subject = "Account Confirmation Live-Poll"
        val confirmationUrl = event.appUrl + "/v0/account/confirm?token=$token"
        val message = "Please confirm your email: "

        javaMailSender.send(SimpleMailMessage().apply {
            setTo(recipientAddress)
            setSubject(subject)
            setText("$message\r\n$serverUrl$confirmationUrl")
        })
    }

}