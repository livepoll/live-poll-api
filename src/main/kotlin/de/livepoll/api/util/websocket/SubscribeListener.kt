package de.livepoll.api.util.websocket

import de.livepoll.api.entity.db.PollItem
import de.livepoll.api.repository.PollItemRepository
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
            if (poll == null) {
                throw ResponseStatusException(HttpStatus.NOT_FOUND)
            } else {
                val url = "/v1/websocket/poll/$slug"
                if (poll.currentItem == null) {
                    messagingTemplate.convertAndSendToUser(event.user!!.name, url, "{\"pollId\":${poll.id}}")
                } else {
                    val pollItemDto = pollItemService.getPollItem(poll.currentItem!!)
                    messagingTemplate.convertAndSendToUser(event.user!!.name, url, pollItemDto)
                }
            }
        } else if (destination.contains("presentation")) {
            val pollId = destination.split("/").last().toLong()
            pollRepository.findById(pollId).orElseThrow {
                val url = "/v1/websocket/presentation/${pollId}"
                messagingTemplate.convertAndSendToUser(event.user!!.name, url, "No current item")
                throw ResponseStatusException(HttpStatus.NOT_FOUND)
            }.run {
                webSocketService.sendItemWithAnswers(this.currentItem!!)
            }
        }
    }
}
