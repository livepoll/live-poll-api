package de.livepoll.api.controller

import de.livepoll.api.entity.db.Poll
import de.livepoll.api.entity.dto.MultipleChoiceItemDtoIn
import de.livepoll.api.entity.dto.MultipleChoiceItemDtoOut
import de.livepoll.api.repository.PollRepository
import de.livepoll.api.service.PollService
import io.swagger.v3.oas.annotations.parameters.RequestBody
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/v0/poll/")
class PollController(
        private val pollService: PollService,
        private val pollRepository: PollRepository
) {

    @PostMapping("/{id}/multiplechoiceitem")
    fun addQuizItem(@PathVariable(name="id") id: Int, @RequestBody newItem: MultipleChoiceItemDtoIn): ResponseEntity<*> {
        val poll = pollRepository.getOne(id)
        if (poll.user.username == SecurityContextHolder.getContext().authentication.name) {
            return ResponseEntity.ok().body(pollService.addMultipleChoiceItem(newItem))
        } else {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to access this user")
        }

    }
}