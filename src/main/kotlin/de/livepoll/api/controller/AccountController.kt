package de.livepoll.api.controller

import de.livepoll.api.entity.dto.UserDtoIn
import de.livepoll.api.entity.jwt.AuthenticationRequest
import de.livepoll.api.exception.EmailNotConfirmedException
import de.livepoll.api.exception.UserExistsException
import de.livepoll.api.service.AccountService
import de.livepoll.api.util.toDbEntity
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.servlet.http.HttpServletRequest

@RestController
@Api(value = "Account", description = "Account-related processing", tags = ["Account"])
@RequestMapping("/v1/account")
class AccountController(
    private val accountService: AccountService,
    private val authenticationManager: AuthenticationManager,
) {

    @ApiOperation(value = "Register new user", tags = ["Account"])
    @PostMapping("/register")
    fun createNewUser(@RequestBody newUser: UserDtoIn): ResponseEntity<*> {
        return try {
            accountService.createAccount(newUser.toDbEntity())
            ResponseEntity.ok("Please confirm email")
        } catch (e: UserExistsException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username or email already exists", e)
        }
    }

    @ApiOperation(value = "Confirm registration", tags = ["Account"])
    @GetMapping("/confirm")
    fun confirmRegistration(@RequestParam("token") token: String): String {
        return if (accountService.confirmAccount(token)) "Registration confirmed" else "Token expired"
    }

    @ApiOperation(value = "Login", tags = ["Account"])
    @PostMapping("/login")
    fun login(@RequestBody authRequest: AuthenticationRequest): ResponseEntity<*>? {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(authRequest.username, authRequest.password)
            )
            return accountService.login(authRequest.username)
        } catch (err: EmailNotConfirmedException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please confirm your email")
        } catch (ex: UsernameNotFoundException) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong username or password")
        } catch (ex: Exception) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong username or password")
        }
    }

    @ApiOperation(value = "Logout", tags = ["Account"])
    @PutMapping("/logout")
    fun logout(httpServletRequest: HttpServletRequest): ResponseEntity<*> {
        return accountService.logout(httpServletRequest)
    }
}
