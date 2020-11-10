package de.livepoll.api.controller

import de.livepoll.api.entity.jwt.AuthenticationRequest
import de.livepoll.api.service.JwtUserDetailsService
import de.livepoll.api.util.JwtUtil
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v0/authenticate")
class AuthenticationController(
        private val jwtUserDetailsService: JwtUserDetailsService,
        private val jwtUtil: JwtUtil,
        private val authenticationManager: AuthenticationManager
) {

    @PostMapping
    fun createAuthenticationToken(@RequestBody authRequest: AuthenticationRequest): ResponseEntity<*>? {
        try {
            authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password)
            )
            val userDetails = jwtUserDetailsService.loadUserByUsername(authRequest.username)
            return ResponseEntity.ok(jwtUtil.generateToken(userDetails))
        } catch (ex: Exception) {
            throw Exception("Wrong password")
        }
    }
}
