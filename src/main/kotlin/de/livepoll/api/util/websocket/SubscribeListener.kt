package de.livepoll.api.util.websocket

import de.livepoll.api.repository.PollRepository
import de.livepoll.api.service.PollItemService
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
    private val pollItemService: PollItemService
) : ApplicationListener<SessionSubscribeEvent> {

    @Transactional
    override fun onApplicationEvent(event: SessionSubscribeEvent) {
        if( event.message.headers["simpDestination"].toString().contains("poll")){
            val slug = event.message.headers["simpDestination"].toString().split("/").last()
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
        }
    }
}
