package de.livepoll.api.util

import de.livepoll.api.service.AccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.springframework.util.ResourceUtils
import java.util.*
import javax.mail.internet.MimeMessage


@Component
class AccountListener: ApplicationListener<OnCreateAccountEvent> {
    
    private val frontendUrl = System.getenv("LIVE_POLL_FRONTEND_URL")

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
        val subject = "Your Live-Poll registration"
        val confirmationUrl = "$frontendUrl/activate/$token"

        val mimeMessage: MimeMessage = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true)
        helper.setFrom("noreply@live-poll.de")
        helper.setTo(recipientAddress)
        helper.setSubject(subject)
        helper.setText(
                "<html>"
                        + "<body>"
                        + "<img src='cid:logo' style='float:top;width:150px;height:150px;'/>"
                        + "<div style='margin:1rem'/>"
                        + "<div>Dear ${user.username},"
                        + "<div>thank you for using Live-Poll."
                        + "<div style='margin:1rem'/>"
                        + "<div>Live-Poll is an open-source live-polling application that you can use totally free, no matter if you are a private person, school, university, society, small or big business etc. Our idea arose from the lack of free live voting/polling software on the Internet that has a nice user flow and is easy to use.</div>"
                        + "<div style='margin:1rem'/>"
                        + "<div>Please confirm your registration by clicking <a href='$confirmationUrl'>here</a>.</div>"
                        + "<div style='margin:1rem'/>"
                        + "<div>Best regards,</div>"
                        + "<div>Live-Poll</div>"
                        + "</div>"
                        + "</div></body>"
                        + "</html>", true)
        helper.addInline("logo",
                ResourceUtils.getFile("classpath:logo.png"))

        javaMailSender.send(mimeMessage)
    }
}