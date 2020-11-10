package de.livepoll.api.service

import de.livepoll.api.entity.db.User
import de.livepoll.api.entity.db.VerificationToken
import de.livepoll.api.exception.UserExistsException
import de.livepoll.api.repository.UserRepository
import de.livepoll.api.repository.VerificationTokenRepository
import de.livepoll.api.util.OnCreateAccountEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class AccountService {

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var eventPublisher: ApplicationEventPublisher

    @Autowired
    private lateinit var verificationTokenRepository: VerificationTokenRepository

    fun createAccount(user: User): User {
        return user.apply {
            if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email))
                throw UserExistsException("Username or email already exists")
            password = passwordEncoder.encode(password)
            accountStatus = false
            roles = "ROLE_USER"
            userRepository.saveAndFlush(this)
            eventPublisher.publishEvent(OnCreateAccountEvent(this, ""))
        }
    }

    fun createVerificationToken(user: User, token: String) {
        val verificationToken = VerificationToken(0, token, user.username, calculateExpiryDate())
        verificationTokenRepository.saveAndFlush(verificationToken)
    }

    fun confirmAccount(token: String): Boolean {
        val verificationToken = verificationTokenRepository.findByToken(token)
        return if (verificationToken.expiryDate.after(Date())) {
            val user = userRepository.findByUsername(verificationToken.username)
            user.accountStatus = true
            verificationTokenRepository.delete(verificationToken)
            true
        } else false
    }

    private fun calculateExpiryDate(): Date {
        return Calendar.getInstance().run {
            add(Calendar.MINUTE, 60 * 24)
            time
        }
    }
}
