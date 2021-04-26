package de.livepoll.api.service

import de.livepoll.api.entity.db.PollItem
import de.livepoll.api.repository.PollItemRepository
import de.livepoll.api.repository.PollRepository
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Controller

@Controller
class WebSocketService(
        private val messagingTemplate: SimpMessageSendingOperations
) {

    fun sendCurrenItem(slug: String, currentItemId: Int) {
        //val item: PollItem = ?? TODO find current item
        val url = "/poll/" + slug
        messagingTemplate.convertAndSend(url)  //(url, item)
    }
}