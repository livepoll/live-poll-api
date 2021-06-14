package de.livepoll.api.service

import de.livepoll.api.entity.db.BlockedToken
import de.livepoll.api.entity.db.PollItem
import de.livepoll.api.entity.db.User
import de.livepoll.api.entity.db.VerificationToken
import de.livepoll.api.exception.EmailNotConfirmedException
import de.livepoll.api.exception.UserExistsException
import de.livepoll.api.repository.*
import de.livepoll.api.util.JwtUtil
import de.livepoll.api.util.OnCreateAccountEvent
import de.livepoll.api.util.jwtCookie.CookieCipher
import de.livepoll.api.util.jwtCookie.CookieUtil
import org.springframework.context.ApplicationEventPublisher
import org.springframework.dao.DataAccessException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.persistence.EntityNotFoundException
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
    private val blockedTokenRepository: BlockedTokenRepository,
    private val pollRepository: PollRepository,
    private val pollItemRepository: PollItemRepository<PollItem>
) {

    /**
     * Create a new account.
     *
     * @param user the new user object that should be saved in the database
     */
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

    /**
     * Create a test user for postman.
     */
    fun createPostmanAccount() {
        if (userRepository.existsByUsername("postman")) return
        val user = User(
            0, "postman", "noreply@live-poll.de", passwordEncoder.encode("1234"),
            true, "ROLE_USER", emptyList()
        )
        userRepository.saveAndFlush(user)
    }

    /**
     * Create a verification token for account confirmation and save it in the database.
     *
     * @param user the user for whom the token is generated
     * @param token the token string
     */
    fun createVerificationToken(user: User, token: String) {
        val verificationToken = VerificationToken(0, token, user.username, calculateExpiryDate())
        verificationTokenRepository.saveAndFlush(verificationToken)
    }

    /**
     * Confirm an account.
     *
     * @param token the token string to confirm the new account
     */
    fun confirmAccount(token: String): Boolean {
        try {
            val verificationToken = verificationTokenRepository.findByToken(token)
            if (verificationToken.expiryDate.after(Date())) {
                val user = userRepository.findByUsername(verificationToken.username)
                user?.isAccountEnabled = true
                verificationTokenRepository.delete(verificationToken)
                return true
            }
            return false
        } catch (ex:EmptyResultDataAccessException) {
            return false
        }

    }

    private fun calculateExpiryDate() = Calendar.getInstance()
        .apply { add(Calendar.MINUTE, 60 * 24) }
        .time

    /**
     * Create jwt token for login. This method does not check if the password is correct. This is done with the authentication manager before this method is called.
     *
     * @param username the name of the user you want to login with
     * @return a ResponseEntity object is returned with an encrypted jwt token for future authentication in the header
     */
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

    /**
     * Logout
     *
     * @param request the http request that contains the jwt token that should be blocked
     */
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

    fun checkAuthorizationByUsername(username: String): Boolean {
        if (SecurityContextHolder.getContext().authentication.name == username)
            return true
        throw ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized")
    }

    /**
     * Check if user should have access to this poll.
     *
     * @param id the id of the poll
     * @return a boolean that indicates whether access is permitted or not
     */
    fun checkAuthorizationByPollId(id: Long): Boolean {
        try {
            if (SecurityContextHolder.getContext().authentication.name == pollRepository.getOne(id).user.username)
                return true
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized")
        } catch (ex: DataAccessException) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized")
        }
    }

    /**
     * Check if user should have access to this poll item.
     *
     * @param id the id of the poll item
     * @return a boolean that indicates whether access is permitted or not
     */
    fun checkAuthorizationByPollItemId(id: Long): Boolean {
        try {
            if (SecurityContextHolder.getContext().authentication.name == pollItemRepository.getOne(id).poll.user.username)
                return true
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized")
        } catch (ex: EntityNotFoundException) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized")
        }

    }
}
