package de.livepoll.api.controller

import de.livepoll.api.entity.db.User
import de.livepoll.api.entity.dto.PollDtoIn
import de.livepoll.api.entity.dto.PollDtoOut
import de.livepoll.api.entity.dto.PollItemDtoOut
import de.livepoll.api.service.AccountService
import de.livepoll.api.service.PollService
import de.livepoll.api.util.toDtoOut
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@Api(value = "Poll", description = "A user's poll, encompassing multiple poll items", tags = ["Poll"])
@RequestMapping("/v1/polls")
class PollController(
    private val pollService: PollService,
    private val accountService: AccountService
) {

    //-------------------------------------------- Create --------------------------------------------------------------

    @ApiOperation(value = "Create poll", tags = ["Poll"])
    @PostMapping
    fun createPoll(@RequestBody newPoll: PollDtoIn, @AuthenticationPrincipal user: User): ResponseEntity<*> {
        val addedPoll = pollService.createPoll(newPoll, user.id)
        return ResponseEntity.created(URI(newPoll.name)).body(addedPoll)
    }

    //--------------------------------------------- Get ----------------------------------------------------------------

    @ApiOperation(value = "Get poll", tags = ["Poll"])
    @GetMapping("/{id}")
    fun getPoll(@PathVariable(name = "id") pollId: Long, @AuthenticationPrincipal user: User): PollDtoOut {
        accountService.checkAuthorizationByPollId(pollId)
        return pollService.getPoll(pollId)
    }

    @ApiOperation(value = "Get polls", tags = ["Poll"])
    @GetMapping
    fun getPolls(@AuthenticationPrincipal user: User): ResponseEntity<*> {
        val pollsOut = user.polls.map { it.toDtoOut() }
        return ResponseEntity.ok().body(pollsOut)
    }

    @ApiOperation(value = "Get poll items", tags = ["Poll item"])
    @GetMapping("/{id}/poll-items")
    fun getPollItems(@PathVariable(name = "id") pollId: Long): List<PollItemDtoOut> {
        accountService.checkAuthorizationByPollId(pollId)
        return pollService.getPollItemsForPoll(pollId)
    }

    @ApiOperation(value = "Get next poll item for presentation", tags = ["Poll presentation"])
    @GetMapping("/{id}/next-item")
    fun getNextPollItem(@PathVariable(name = "id") pollId: Long, @AuthenticationPrincipal user: User): PollDtoOut {
        accountService.checkAuthorizationByPollId(pollId)
        return pollService.getNextPollItem(pollId)
    }

    //-------------------------------------------- Update --------------------------------------------------------------

    @ApiOperation(value = "Update slug", tags = ["Poll"])
    @PutMapping("/{id}")
    fun updatePoll(@PathVariable(name = "id") pollId: Long, @RequestBody updatedPoll: PollDtoIn): PollDtoOut {
        accountService.checkAuthorizationByPollId(pollId)
        return pollService.updatePoll(pollId, updatedPoll)
    }

    //-------------------------------------------- Delete --------------------------------------------------------------

    @ApiOperation(value = "Delete poll", tags = ["Poll"])
    @DeleteMapping("/{id}")
    fun deletePoll(@PathVariable(name = "id") pollId: Long) {
        accountService.checkAuthorizationByPollId(pollId)
        return pollService.deletePoll(pollId)
    }
}
