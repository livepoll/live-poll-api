package de.livepoll.api.controller

import de.livepoll.api.entity.dto.PollDtoIn
import de.livepoll.api.repository.UserRepository
import de.livepoll.api.service.PollService
import de.livepoll.api.util.toDtoOut
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/v0/users")
class UserController(
        private val userRepository: UserRepository,
        private val pollService: PollService
) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable(name = "id") userId: Int, response: HttpServletResponse): ResponseEntity<*> {
        val user = userRepository.getOne(userId).toDtoOut()
        return if (user.username.equals(SecurityContextHolder.getContext().authentication.name)) {
            ResponseEntity.ok().body(user)
        } else {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to access this user")
        }
    }

    @GetMapping("/{id}/polls")
    fun getPollsForUser(@PathVariable(name = "id") userId: Int): ResponseEntity<*> {
        val user = userRepository.getOne(userId)
        return if (user.username.equals(SecurityContextHolder.getContext().authentication.name)) {
            ResponseEntity.ok().body(user.polls.map { it.toDtoOut() })
        } else {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to access this user")
        }
    }

    @PostMapping("/{id}/poll")
    fun createPollForUser(@PathVariable(name = "id") userId: Int, @RequestBody newPoll: PollDtoIn): ResponseEntity<*> {
        val user = userRepository.getOne(userId)
        return if (user.username.equals(SecurityContextHolder.getContext().authentication.name)) {
            pollService.createPollEntity(newPoll, userId)
            return ResponseEntity.ok("Poll created")
        } else {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to access this user")
        }


    }

}
