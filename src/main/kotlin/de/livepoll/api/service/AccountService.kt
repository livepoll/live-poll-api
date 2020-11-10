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
        if (userRepository.countUsersWithUsername(user.username) != 0 || userRepository.countUsersWithEmail(user.email) != 0) {
            throw UserExistsException("Username or email already exists")
        }
        user.password = passwordEncoder.encode(user.password)
        user.accountStatus = false
        user.roles = "ROLE_USER"
        userRepository.saveAndFlush(user)
        eventPublisher.publishEvent(OnCreateAccountEvent(user, ""))
        return user
    }

    fun createVerificationToken(user: User, token: String) {
        val verificationToken = VerificationToken(0, token, user.username, calculateExpiryDate())
        verificationTokenRepository.saveAndFlush(verificationToken)
    }

    fun confirmAccount(token: String) {
        val verificationToken = verificationTokenRepository.findByToken(token)
        if (verificationToken.expiryDate.after(Date())) {
            val user = userRepository.findByUsername(verificationToken.username)
            user.accountStatus = true
            verificationTokenRepository.delete(verificationToken)
        }
    }

    private fun calculateExpiryDate(): Date {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, 60 * 24)
        return cal.time
    }

}
