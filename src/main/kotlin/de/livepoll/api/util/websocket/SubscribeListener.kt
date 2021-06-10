package de.livepoll.api.util.websocket

import de.livepoll.api.repository.PollRepository
import de.livepoll.api.service.PollItemService
import de.livepoll.api.service.WebSocketService
import org.springframework.context.ApplicationListener
import org.springframework.http.HttpStatus
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.socket.messaging.SessionSubscribeEvent

@Component
class SubscribeListener(
    private val messagingTemplate: SimpMessageSendingOperations,
    private val pollRepository: PollRepository,
    private val pollItemService: PollItemService,
    private val webSocketService: WebSocketService
) : ApplicationListener<SessionSubscribeEvent> {

    @Transactional
    override fun onApplicationEvent(event: SessionSubscribeEvent) {
        val destination = event.message.headers["simpDestination"].toString()
        if (destination.contains("poll")) {
            val slug = destination.split("/").last()
            val poll = pollRepository.findBySlug(slug)
            val url = "/v1/websocket/poll/$slug"
            if (poll == null) {
                sendErrorMessage(event.user!!.name, url, "Error in the participant endpoint")
                throw ResponseStatusException(HttpStatus.NOT_FOUND)
            } else {
                if (poll.currentItem == null) {
                    messagingTemplate.convertAndSendToUser(event.user!!.name, url, "{\"pollId\":${poll.id}}")
                    throw ResponseStatusException(HttpStatus.NOT_FOUND)
                } else {
                    val pollItemDto = pollItemService.getPollItem(poll.currentItem!!)
                    messagingTemplate.convertAndSendToUser(event.user!!.name, url, pollItemDto)
                }
            }
        } else if (destination.contains("presentation")) {
            val pollId = destination.split("/").last().toLong()
            val url = "/v1/websocket/presentation/${pollId}"
            pollRepository.findById(pollId).orElseThrow {
                sendErrorMessage(event.user!!.name, url, "Error in the presentation endpoint")
                throw ResponseStatusException(HttpStatus.NOT_FOUND)
            }.run {
                if (this.currentItem != null) {
                    webSocketService.sendItemWithAnswers(this.currentItem!!)
                } else {
                    sendErrorMessage(event.user!!.name, url, "Error in the presentation endpoint")
                    throw ResponseStatusException(HttpStatus.NOT_FOUND)
                }
            }
        }
    }

    private fun sendErrorMessage(username: String, url: String, errorMessage: String) {
        messagingTemplate.convertAndSendToUser(username, url, "{\"error\":\"$errorMessage\"}")
    }

}
