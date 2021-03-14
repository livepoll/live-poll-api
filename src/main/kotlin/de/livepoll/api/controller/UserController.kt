package de.livepoll.api.controller

import de.livepoll.api.entity.dto.PollDtoIn
import de.livepoll.api.repository.UserRepository
import de.livepoll.api.service.PollService
import de.livepoll.api.util.toDtoOut
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
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
        val user = userRepository.findById(userId)
        if (user.isPresent) {
            val userOut = user.get().toDtoOut()
            if (userOut.username == SecurityContextHolder.getContext().authentication.name) {
                return ResponseEntity.ok().body(userOut)
            }
        }
        // Send unauthorized even if the user does not exist (to avoid exploitation of this endpoint)
        throw ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this user")
    }

    @GetMapping("/{id}/polls")
    fun getPollsForUser(@PathVariable(name = "id") userId: Int): ResponseEntity<*> {
        val user = userRepository.getOne(userId)
        if (user.username == SecurityContextHolder.getContext().authentication.name) {
            return ResponseEntity.ok().body(user.polls.map { it.toDtoOut() })
        } else {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this user")
        }
    }

    @GetMapping("/{id}/polls/{pollId}")
    fun getPoll(@PathVariable(name = "id") userId: Int, @PathVariable(name = "pollId") pollId: Int): ResponseEntity<*> {
        return pollService.getPoll(pollId)
    }

    @PostMapping("/{id}/poll")
    fun createPollForUser(@PathVariable(name = "id") userId: Int, @RequestBody newPoll: PollDtoIn): ResponseEntity<*> {
        val user = userRepository.getOne(userId)
        if (user.username == SecurityContextHolder.getContext().authentication.name) {
            pollService.createPollEntity(newPoll, userId)

            val response: HashMap<String, String> = HashMap()
            response["message"] = "Poll created"
            return ResponseEntity(response, HttpStatus.CREATED)
        } else {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this user")
        }
    }
}
