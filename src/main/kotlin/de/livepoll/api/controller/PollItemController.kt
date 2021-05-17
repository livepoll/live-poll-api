package de.livepoll.api.controller

import de.livepoll.api.entity.dto.MultipleChoiceItemDtoIn
import de.livepoll.api.entity.dto.OpenTextItemDtoIn
import de.livepoll.api.entity.dto.PollItemDtoOut
import de.livepoll.api.entity.dto.QuizItemDtoIn
import de.livepoll.api.service.AccountService
import de.livepoll.api.service.PollItemService
import de.livepoll.api.service.PollService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@Api(value = "Poll item", description = "Poll items, e.g. a multiple choice question", tags = ["Poll item"])
@RequestMapping("/v1/poll-items")
class PollItemController(
    private val pollService: PollService,
    private val pollItemService: PollItemService,
    private val accountService: AccountService
) {

    //-------------------------------------------- Create --------------------------------------------------------------

    @ApiOperation(value = "Create multiple choice item", tags = ["Poll item"])
    @PostMapping("/multiple-choice")
    fun createMultipleChoiceItem(@RequestBody newItem: MultipleChoiceItemDtoIn): ResponseEntity<*> {
        val addedItem = pollItemService.createMultipleChoiceItem(newItem)
        return ResponseEntity.created(URI(newItem.pollId.toString())).body(addedItem)
    }

    @ApiOperation(value = "Create quiz item", tags = ["Poll item"])
    @PostMapping("/quiz")
    fun createQuizItem(@RequestBody newItem: QuizItemDtoIn): ResponseEntity<*> {
        val addedItem = pollItemService.createQuizItem(newItem)
        return ResponseEntity.created(URI(newItem.pollId.toString())).body(addedItem)
    }

    @ApiOperation(value = "Create open text item", tags = ["Poll item"])
    @PostMapping("/open-text")
    fun createOpenTextItem(@RequestBody newItem: OpenTextItemDtoIn): ResponseEntity<*> {
        val addedItem = pollItemService.createOpenTextItem(newItem)
        return ResponseEntity.created(URI(newItem.pollId.toString())).body(addedItem)
    }

    //--------------------------------------------- Get ----------------------------------------------------------------

    @ApiOperation(value = "Get poll item", tags = ["Poll item"])
    @GetMapping("/{id}")
    fun getPollItem(@PathVariable(name = "id") pollItemId: Long): PollItemDtoOut {
        accountService.checkAuthorizationByPollItemId(pollItemId)
        return pollItemService.getPollItem(pollItemId)
    }

    //-------------------------------------------- Update --------------------------------------------------------------

    @ApiOperation(value = "Update multiple choice item", tags = ["Poll item"])
    @PutMapping("/multiple-choice/{pollItemId}")
    fun updateMultipleChoiceItem(@RequestBody updatedItem: MultipleChoiceItemDtoIn, @PathVariable(name="pollItemId") pollItemId: Long): ResponseEntity<*> {
        accountService.checkAuthorizationByPollItemId(pollItemId)
        return ResponseEntity.ok(pollItemService.updateMultipleChoiceItem(pollItemId, updatedItem))
    }

    @ApiOperation(value = "Update quiz item", tags = ["Poll item"])
    @PutMapping("/quiz/{pollItemId}")
    fun updateQuizItem(@RequestBody updatedItem: QuizItemDtoIn, @PathVariable(name="pollItemId") pollItemId: Long): ResponseEntity<*> {
        accountService.checkAuthorizationByPollItemId(pollItemId)
        return ResponseEntity.ok(pollItemService.updateQuizItem(pollItemId, updatedItem))
    }

    @ApiOperation(value = "Update open text item", tags = ["Poll item"])
    @PutMapping("/open-text/{pollItemId}")
    fun updateOpenTextItem(@RequestBody updatedItem: OpenTextItemDtoIn, @PathVariable(name="pollItemId") pollItemId: Long): ResponseEntity<*> {
        accountService.checkAuthorizationByPollItemId(pollItemId)
        return ResponseEntity.ok(pollItemService.updateOpenTextItem(pollItemId, updatedItem))
    }

    //-------------------------------------------- Delete --------------------------------------------------------------

    @ApiOperation(value = "Delete poll item", tags = ["Poll item"])
    @DeleteMapping("/{id}")
    fun deletePollItem(@PathVariable(name = "id") pollItemId: Long): ResponseEntity<*> {
        accountService.checkAuthorizationByPollItemId(pollItemId)
        pollItemService.deleteItem(pollItemId)
        return ResponseEntity.ok("Deleted poll item")
    }
}
