package de.livepoll.api.controller

import de.livepoll.api.entity.db.Poll
import de.livepoll.api.entity.dto.UserDto
import de.livepoll.api.repository.UserRepository
import de.livepoll.api.util.toDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
        private val appVersion: String,
        private val repository: UserRepository
) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Int): UserDto {
        return repository.findById(id).get().toDto()
    }

    @GetMapping("/{id}/polls")
    fun getPollsForUser(@PathVariable id: Int): List<Poll> {
        return repository.getOne(id).polls
    }

}