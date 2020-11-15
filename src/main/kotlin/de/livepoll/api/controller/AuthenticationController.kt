package de.livepoll.api.controller

import de.livepoll.api.entity.jwt.AuthenticationRequest
import de.livepoll.api.exception.EmailNotConfirmedException
import de.livepoll.api.repository.UserRepository
import de.livepoll.api.service.AccountService
import de.livepoll.api.util.toDtoOut
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/v0/authenticate")
class AuthenticationController(
        private val accountService: AccountService,
        private val authenticationManager: AuthenticationManager,
        private val userRepository: UserRepository
) {

    @PostMapping("/login")
    fun createAuthenticationToken(@RequestBody authRequest: AuthenticationRequest): ResponseEntity<*>? {
        try {
            authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password)
            )
            return accountService.login(authRequest.username)
        } catch (_: EmailNotConfirmedException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please confirm your email")
        } catch (_: UsernameNotFoundException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username is wrong")
        } catch (_: Exception) {
            throw Exception("Wrong password")
        }
    }

    @PutMapping("/logout")
    fun logout(httpServletRequest: HttpServletRequest): ResponseEntity<*> {
        return accountService.logout(httpServletRequest)
    }

    @GetMapping("/init")
    fun loadInitialUser(httpServletRequest: HttpServletRequest): ResponseEntity<*> {
        val user = userRepository.findByUsername(SecurityContextHolder.getContext().authentication.name)
        return ResponseEntity.ok().body(user?.toDtoOut())
    }
}
