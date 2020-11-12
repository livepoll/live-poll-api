package de.livepoll.api.controller

import com.sun.mail.iap.Response
import de.livepoll.api.entity.jwt.AuthenticationRequest
import de.livepoll.api.exception.EmailNotConfirmedException
import de.livepoll.api.service.AccountService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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
        } catch (e1: EmailNotConfirmedException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please confirm your email")
        } catch (e2: UsernameNotFoundException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username is wrong")
        } catch (ex: Exception) {
            throw Exception("Wrong password")
        }
    }

    @GetMapping("/logout")
    fun logout(httpServletRequest: HttpServletRequest): ResponseEntity<*> {
        return accountService.logout(httpServletRequest)
    }
}
