package de.livepoll.api.service

import com.fasterxml.jackson.databind.ObjectMapper
import de.livepoll.api.entity.db.*
import de.livepoll.api.entity.dto.PollItemDtoOut
import de.livepoll.api.repository.*
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.user.SimpUserRegistry
import org.springframework.stereotype.Controller


@Controller
class WebSocketService(
        private val messagingTemplate: SimpMessageSendingOperations,
        private val pollItemService: PollItemService,
        private val multipleChoiceItemRepository: MultipleChoiceItemRepository,
        private val openTextItemRepository: OpenTextItemRepository,
        private val quizItemRepository: QuizItemRepository,
        private val simpUserRegistry: SimpUserRegistry
) {
    fun sendCurrenItem(slug: String, currentItemId: Long) {
        val item: PollItemDtoOut = pollItemService.getPollItem(currentItemId);
        val url = "/v1/websocket/poll/" + slug
        simpUserRegistry.users.forEach {
            messagingTemplate.convertAndSendToUser(it.name, url, item)
        }
    }


    fun saveAnswer(pollItemId: Long, payload: String) {
        println("PAYLOAD: " + payload)
        val mapper = ObjectMapper()
        val map: Map<String, String> = mapper.readValue(payload, Map::class.java) as Map<String, String>
        when (getPollItemType(map.get("type")!!)) {

            // Multiple Choice
            PollItemType.MULTIPLE_CHOICE -> {
                val obj: MultipleChoiceItemAnswer = mapper.readValue(payload, MultipleChoiceItemAnswer::class.java)
                val pollItem: MultipleChoiceItem = multipleChoiceItemRepository.getOne(pollItemId)
                println(obj.id)
                println(obj.answerCount)
                println(obj.multipleChoiceItem.question)
                println(obj.selectionOption)
                pollItem.answers.add(obj)
                multipleChoiceItemRepository.saveAndFlush(pollItem)
            }

            // Open text
            PollItemType.OPEN_TEXT -> {
                val obj: OpenTextItemAnswer = mapper.readValue(payload, OpenTextItemAnswer::class.java)
                val pollItem: OpenTextItem = openTextItemRepository.getOne(pollItemId)
                pollItem.answers.add(obj)
                openTextItemRepository.saveAndFlush(pollItem)
            }

            // Quiz
            PollItemType.QUIZ -> {
                val obj: QuizItemAnswer = mapper.readValue(payload, QuizItemAnswer::class.java)
                val pollItem: QuizItem = quizItemRepository.getOne(pollItemId)
                pollItem.answers.add(obj)
                quizItemRepository.saveAndFlush(pollItem)
            }

        }
    }

    private fun getPollItemType(type: String): PollItemType {
        when (type.toLowerCase()) {
            "multiple_choice" -> {
                return PollItemType.MULTIPLE_CHOICE
            }
            "open_text" -> {
                return PollItemType.OPEN_TEXT
            }
            "quiz" -> {
                return PollItemType.QUIZ
            }
        }
        throw Exception("Item type not allowed")
    }
}