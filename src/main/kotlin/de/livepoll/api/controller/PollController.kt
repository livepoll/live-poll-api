package de.livepoll.api.controller

import de.livepoll.api.entity.db.User
import de.livepoll.api.entity.dto.MultipleChoiceItemDtoIn
import de.livepoll.api.entity.dto.PollDtoIn
import de.livepoll.api.entity.dto.QuizItemDtoIn
import de.livepoll.api.service.PollService
import de.livepoll.api.util.toDtoOut
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@Api(value = "Poll", description = "A user's poll, encompassing multiple poll items", tags = ["Poll"])
@RequestMapping("/v1/polls")
class PollController(
    private val pollService: PollService,
) {

    @ApiOperation(value = "Get polls", tags = ["Poll"])
    @GetMapping
    fun getPolls(@AuthenticationPrincipal user: User): ResponseEntity<*> {
        val pollsOut = user.polls.map { it.toDtoOut() }
        return ResponseEntity.ok().body(pollsOut)
    }

    @ApiOperation(value = "Create poll", tags = ["Poll"])
    @PostMapping
    fun createPoll(@RequestBody newPoll: PollDtoIn, @AuthenticationPrincipal user: User): ResponseEntity<*> {
        pollService.createPollEntity(newPoll, user.id)
        val response: HashMap<String, String> = HashMap()
        response["message"] = "Poll created"
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    @ApiOperation(value = "Get poll", tags = ["Poll"])
    @GetMapping("/{id}")
    fun getPoll(@PathVariable(name = "id") pollId: Int, @AuthenticationPrincipal user: User): ResponseEntity<*> {
        return pollService.getPoll(pollId)
    }

    @ApiOperation(value = "Delete poll", tags = ["Poll"])
    @DeleteMapping("/{id}")
    fun deletePoll(@PathVariable(name = "id") pollId: Int) {
        return pollService.deletePoll(pollId)
    }

    @ApiOperation(value = "Get poll items", tags = ["Poll item"])
    @GetMapping("/{id}/poll-items")
    fun getPollItems(@PathVariable(name = "id") pollId: Int): ResponseEntity<*> {
        return pollService.getPollItemsForPoll(pollId)
    }

}
