package de.livepoll.api.controller

import de.livepoll.api.service.PollItemService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v0/items")
class PollItemController(
        private val pollItemService: PollItemService
) {

    @ApiOperation(value = "Get a poll item")
    @GetMapping("/{id}")
    fun getPollItem(
            @ApiParam(name = "type", type = "String", value = "Poll-item type", example = "multiplechoice", required = true)
            @RequestParam(name = "type") itemType: String,

            @ApiParam(name = "id", type = "Int", value = "Poll-item id", example = "1", required = true)
            @PathVariable(name = "id") pollItemId: Int): ResponseEntity<*> {
        return ResponseEntity.ok().body(pollItemService.getPollItem(pollItemId, itemType))
    }
}