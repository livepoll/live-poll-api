package de.livepoll.api.controller

import com.sun.mail.iap.Response
import de.livepoll.api.entity.dto.MultipleChoiceItemDtoIn
import de.livepoll.api.entity.dto.QuizItemDtoIn
import de.livepoll.api.service.PollItemService
import de.livepoll.api.service.PollService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@Api(value = "Poll item", description = "Poll items, e.g. a multiple choice question", tags = ["Poll item"])
@RequestMapping("/v1/poll-items")
class PollItemController(
    private val pollService: PollService,
    private val pollItemService: PollItemService
) {

    @ApiOperation(value = "Get poll item", tags = ["Poll item"])
    @GetMapping("/{id}")
    fun getPollItem(@PathVariable(name = "id") pollItemId: Int): ResponseEntity<*> {
        return ResponseEntity.ok("TODO")
//        pollItemService.getPollItem(pollItemId, itemType)
    }

    @ApiOperation(value = "Delete poll item", tags = ["Poll item"])
    @DeleteMapping("/{id}")
    fun deletePollItem(@PathVariable(name = "id") itemId: Int): ResponseEntity<*> {
        return ResponseEntity.ok("TODO")
//        pollItemService.deleteItem(itemId, itemType)
    }

    @ApiOperation(value = "Create multiple choice item", tags = ["Poll item"])
    @PostMapping("/multiple-choice")
    fun createMultipleChoiceItem(@RequestBody newItem: MultipleChoiceItemDtoIn): ResponseEntity<*> {
        val addedItem = pollService.createMultipleChoiceItem(newItem)
        return ResponseEntity.ok().body(addedItem)
    }

    @ApiOperation(value = "Create quiz item", tags = ["Poll item"])
    @PostMapping("/quiz")
    fun createQuizItem(@RequestBody newItem: QuizItemDtoIn): ResponseEntity<*> {
        val addedItem = pollService.createQuizItem(newItem)
        return ResponseEntity.ok().body(addedItem)
    }

}
