package de.livepoll.api.service

import de.livepoll.api.entity.db.BlockedToken
import de.livepoll.api.entity.db.User
import de.livepoll.api.entity.db.VerificationToken
import de.livepoll.api.exception.EmailNotConfirmedException
import de.livepoll.api.exception.UserExistsException
import de.livepoll.api.repository.BlockedTokenRepository
import de.livepoll.api.repository.UserRepository
import de.livepoll.api.repository.VerificationTokenRepository
import de.livepoll.api.util.JwtUtil
import de.livepoll.api.util.OnCreateAccountEvent
import de.livepoll.api.util.jwtCookie.CookieCipher
import de.livepoll.api.util.jwtCookie.CookieUtil
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
class AccountService(
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val verificationTokenRepository: VerificationTokenRepository,
    private val cookieUtil: CookieUtil,
    private val jwtUtil: JwtUtil,
    private val jwtUserDetailsService: JwtUserDetailsService,
    private val cookieCipher: CookieCipher,
    private val blockedTokenRepository: BlockedTokenRepository
) {

    fun createAccount(user: User): User {
        return user.apply {
            if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email))
                throw UserExistsException("Username or email already exists")
            password = passwordEncoder.encode(password)
            isAccountEnabled = false
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
        if (verificationToken.expiryDate.after(Date())) {
            val user = userRepository.findByUsername(verificationToken.username)
            user?.isAccountEnabled = true
            verificationTokenRepository.delete(verificationToken)
            return true
        }
        return false
    }

    private fun calculateExpiryDate() = Calendar.getInstance()
        .apply { add(Calendar.MINUTE, 60 * 24) }
        .time

    fun login(username: String): ResponseEntity<*> {
        val user = userRepository.findByUsername(username) ?: userRepository.findByEmail(username)
        user?.run {
            if (isAccountEnabled) {
                val userDetails = jwtUserDetailsService.loadUserByUsername(username)
                val responseHeaders = HttpHeaders()
                responseHeaders.add(
                    HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(
                        jwtUtil.generateToken(userDetails)
                    ).toString()
                )

                val response: HashMap<String, String> = HashMap()
                response["message"] = "Authentication successful"
                return ResponseEntity.ok().headers(responseHeaders).body(response)
            } else {
                throw EmailNotConfirmedException("Email is not confirmed")
            }
        }
        throw UsernameNotFoundException("User not found")
    }

    fun logout(request: HttpServletRequest): ResponseEntity<*> {
        SecurityContextHolder.getContext().authentication = null
        val accessTokenCookieName = System.getenv("LIVE_POLL_JWT_AUTH_COOKIE_NAME")
        request.cookies.forEach {
            if (accessTokenCookieName == it.name) {
                val token = cookieCipher.decrypt(it.value)
                blockedTokenRepository.saveAndFlush(BlockedToken(0, token, jwtUtil.extractExpiration(token)))
            }
        }
        val responseHeaders = HttpHeaders()
        responseHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.deleteAccessTokenCookie().toString())
        val response: HashMap<String, String> = HashMap()
        response["message"] = "Logout successful"
        return ResponseEntity.ok().headers(responseHeaders).body(response)
    }
}
