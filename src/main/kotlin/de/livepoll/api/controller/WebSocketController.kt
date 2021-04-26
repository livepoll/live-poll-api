package de.livepoll.api.controller

import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller

@Controller
class WebSocketController(

) {
    @MessageMapping("{pollId}")
    fun processAnswer(@DestinationVariable pollId: Int, @Payload answer: String) {
        //TODO store answer
    }
}