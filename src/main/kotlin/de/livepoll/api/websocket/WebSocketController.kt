package de.livepoll.api.websocket

import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller

@Controller
class WebSocketController(
        private val webSocketService: WebSocketService
) {
    @MessageMapping("/{pollItemId}")
    fun processAnswer(@DestinationVariable pollItemId: Long, @Payload answer: String) {
        webSocketService.saveAnswer(pollItemId, answer)
        webSocketService.sendItemWithAnswers(pollItemId)
    }
}