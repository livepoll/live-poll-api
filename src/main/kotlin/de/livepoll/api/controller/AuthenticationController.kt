package de.livepoll.api.controller

import de.livepoll.api.entity.jwt.AuthenticationRequest
import de.livepoll.api.service.JwtUserDetailsService
import de.livepoll.api.util.JwtUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody


@Controller
class AuthenticationController {

    @Autowired
    private lateinit var jwtUserDetailsService: JwtUserDetailsService

    @Autowired
    private lateinit var jwtUtil: JwtUtil

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @PostMapping("/authenticate")
    fun createAuthenticationToken(@RequestBody authRequest: AuthenticationRequest): ResponseEntity<*>? {
        try {
            authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password)
            )
        } catch (ex: Exception) {
        }finally {
            val userDetails = jwtUserDetailsService.loadUserByUsername(authRequest.username)
            return ResponseEntity.ok(jwtUtil.generateToken(userDetails))
        }

    }


}