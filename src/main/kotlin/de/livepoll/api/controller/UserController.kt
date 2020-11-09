package de.livepoll.api.controller

import de.livepoll.api.entity.db.Poll
import de.livepoll.api.entity.dto.UserDto
import de.livepoll.api.exception.UserExistsException
import de.livepoll.api.repository.UserRepository
import de.livepoll.api.service.UserService
import de.livepoll.api.util.apiVersion
import de.livepoll.api.util.toDao
import de.livepoll.api.util.toDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v${apiVersion}/users")
class UserController(
        private val repository: UserRepository,
        private val userService: UserService
) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Int): UserDto {
        return repository.findById(id).get().toDto()
    }

    @GetMapping("/{id}/polls")
    fun getPollsForUser(@PathVariable id: Int): List<Poll> {
        return repository.getOne(id).polls
    }

    @PostMapping("/register")
    fun createNewAccount(@RequestBody newUser: UserDto): ResponseEntity<*> {
        try {
            userService.createAccount(newUser.toDao())
        } catch (e: UserExistsException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username or email already exists", e)
        }
        return ResponseEntity.ok("Please confirm email")
    }

    @GetMapping("/accountConfirm")
    fun accountConfirmation(@RequestParam("token") token: String): String {
        userService.confirmAccount(token)
        return "Your account has been confirmed"
    }

}
