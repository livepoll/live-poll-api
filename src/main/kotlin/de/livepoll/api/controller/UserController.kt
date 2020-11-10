package de.livepoll.api.controller

import de.livepoll.api.entity.dto.PollDtoIn
import de.livepoll.api.entity.dto.PollDtoOut
import de.livepoll.api.entity.dto.UserDtoOut
import de.livepoll.api.repository.UserRepository
import de.livepoll.api.service.PollService
import de.livepoll.api.util.toDtoOut
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v0/users")
class UserController(
        private val userRepository: UserRepository,
        private val pollService: PollService
) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Int): UserDtoOut {
        return userRepository.getOne(id).toDtoOut()
    }

    @GetMapping("/{id}/polls")
    fun getPollsForUser(@PathVariable(name = "id") userId: Int): List<PollDtoOut> {
        return userRepository.getOne(userId).polls.map { it.toDtoOut() }
    }

    @PostMapping("/{id}/poll")
    fun createPollForUser(@PathVariable(name = "id") userId: Int, @RequestBody newPoll: PollDtoIn): ResponseEntity<*> {
        pollService.createPollEntity(newPoll, userId)
        return ResponseEntity.ok("Poll created")
    }

}
