package de.livepoll.api.controller

import de.livepoll.api.entity.dto.MultipleChoiceItemDtoIn
import de.livepoll.api.repository.PollRepository
import de.livepoll.api.service.PollItemService
import de.livepoll.api.service.PollService
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/v0/polls")
class PollController(
        private val pollService: PollService,
        private val pollRepository: PollRepository,
        private val pollItemService: PollItemService
) {

    @PostMapping("/{id}/multiplechoiceitem")
    fun addMultipleChoiceItem(@PathVariable(name = "id") pollId: Int, @RequestBody newItem: MultipleChoiceItemDtoIn): ResponseEntity<*> {
        if (pollId != newItem.pollId) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Different poll IDs in Pathvariable and Requestbody")
        }
        val poll = pollRepository.getOne(pollId)
        if (poll.user.username == SecurityContextHolder.getContext().authentication.name) {
            return ResponseEntity.ok().body(pollService.addMultipleChoiceItem(newItem))
        } else {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to access this user")
        }
    }

    @PostMapping("/{id}/quizitem")
    fun addQuizItem(@PathVariable(name = "id") pollId: Int, @RequestBody newItem: MultipleChoiceItemDtoIn): ResponseEntity<*> {
        if (pollId != newItem.pollId) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Different poll IDs in Pathvariable and Requestbody")
        }
        val poll = pollRepository.getOne(pollId)
        if (poll.user.username == SecurityContextHolder.getContext().authentication.name) {
            return ResponseEntity.ok().body(pollService.addQuizItem(newItem))
        } else {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not allowed to access this user")
        }
    }
}