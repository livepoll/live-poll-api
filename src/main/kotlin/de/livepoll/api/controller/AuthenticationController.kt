package de.livepoll.api.controller

import de.livepoll.api.entity.jwt.AuthenticationRequest
import de.livepoll.api.service.AccountService
import de.livepoll.api.service.JwtUserDetailsService
import de.livepoll.api.util.JwtUtil
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v0/authenticate")
class AuthenticationController(
        private val accountService: AccountService,
        private val authenticationManager: AuthenticationManager
) {

    @PostMapping
    fun createAuthenticationToken(@RequestBody authRequest: AuthenticationRequest): ResponseEntity<*>? {
        try {
            val authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password)
            )
            return accountService.login(authRequest.username)
        } catch (ex: Exception) {
            throw Exception("Wrong password")
        }
    }
}
