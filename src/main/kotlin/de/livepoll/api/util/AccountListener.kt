package de.livepoll.api.util

import de.livepoll.api.service.AccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import java.util.*

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
        val confirmationUrl = event.appUrl + "/accountConfirm?token="+token
        val message = "Please confirm your email: "

        val email = SimpleMailMessage()
        email.setTo(recipientAddress)
        email.setSubject(subject)
        email.setText(message+"\r\n"+serverUrl+confirmationUrl)
        javaMailSender.send(email)
    }

}