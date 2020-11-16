package de.livepoll.api.controller

import de.livepoll.api.entity.dto.UserDtoIn
import de.livepoll.api.exception.UserExistsException
import de.livepoll.api.service.AccountService
import de.livepoll.api.util.toDbEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/v0/account")
class AccountController(
        private val accountService: AccountService
) {

    @PostMapping("/register")
    fun createNewAccount(@RequestBody newUser: UserDtoIn): ResponseEntity<*> {
        return try {
            accountService.createAccount(newUser.toDbEntity())
            ResponseEntity.ok("Please confirm email")
        } catch (e: UserExistsException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username or email already exists", e)
        }
    }

    @GetMapping("/confirm")
    fun accountConfirmation(@RequestParam("token") token: String): String {
        return if (accountService.confirmAccount(token)) "Your account has been confirmed" else "Token expired"
    }
}
