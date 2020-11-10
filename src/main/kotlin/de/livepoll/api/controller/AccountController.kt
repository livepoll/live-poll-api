package de.livepoll.api.controller

import de.livepoll.api.entity.dto.UserDtoIn
import de.livepoll.api.exception.UserExistsException
import de.livepoll.api.service.AccountService
import de.livepoll.api.service.toDbEntity
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/v0/account")
class AccountController(
        private val accountService: AccountService
) {

    @PostMapping("/register")
    fun createNewAccount(@RequestBody newUser: UserDtoIn): ResponseEntity<*> {
        try {
            accountService.createAccount(newUser.toDbEntity())
        } catch (e: UserExistsException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username or email already exists", e)
        }
        return ResponseEntity.ok("Please confirm email")
    }

    @GetMapping("/confirm")
    fun accountConfirmation(@RequestParam("token") token: String): String {
        accountService.confirmAccount(token)
        return "Your account has been confirmed"
    }

}
