package de.livepoll.api.service


import com.fasterxml.jackson.databind.ObjectMapper
import de.livepoll.api.entity.db.*
import de.livepoll.api.entity.dto.MultipleChoiceItemParticipantAnswerDtoIn
import de.livepoll.api.entity.dto.OpenTextItemParticipantAnswerDtoIn
import de.livepoll.api.entity.dto.PollItemDtoOut
import de.livepoll.api.entity.dto.QuizItemParticipantAnswerDtoIn
import de.livepoll.api.repository.*
import de.livepoll.api.util.toDbEntity
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.user.SimpUserRegistry
import org.springframework.stereotype.Controller


@Controller
class WebSocketService(
        private val messagingTemplate: SimpMessageSendingOperations,
        private val pollItemService: PollItemService,
        private val openTextItemAnswerRepository: OpenTextItemAnswerRepository,
        private val simpUserRegistry: SimpUserRegistry,
        private val multipleChoiceItemAnswerRepository: MultipleChoiceItemAnswerRepository,
        private val quizItemAnswerRepository: QuizItemAnswerRepository,
        private val openTextItemRepository: OpenTextItemRepository
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
        val type: String = mapper.readValue(payload, Map::class.java).get("type").toString()
        when (getPollItemType(type)) {

            // Multiple Choice
            PollItemType.MULTIPLE_CHOICE -> {
                val obj: MultipleChoiceItemParticipantAnswerDtoIn = mapper.readValue(payload, MultipleChoiceItemParticipantAnswerDtoIn::class.java)
                val multipleChoiceItemAnswer = multipleChoiceItemAnswerRepository.getOne(obj.id)
                multipleChoiceItemAnswer.answerCount++
                multipleChoiceItemAnswerRepository.saveAndFlush(multipleChoiceItemAnswer)
            }

            // Open text
            PollItemType.OPEN_TEXT -> {
                val obj: OpenTextItemParticipantAnswerDtoIn = mapper.readValue(payload, OpenTextItemParticipantAnswerDtoIn::class.java)
                val pollItem = openTextItemRepository.getOne(pollItemId)
                openTextItemAnswerRepository.saveAndFlush(obj.toDbEntity(pollItem))
            }

            // Quiz
            PollItemType.QUIZ -> {
                val obj: QuizItemParticipantAnswerDtoIn = mapper.readValue(payload, QuizItemParticipantAnswerDtoIn::class.java)
                val quizItemAnswer = quizItemAnswerRepository.getOne(obj.id)
                quizItemAnswer.answerCount++
                quizItemAnswerRepository.saveAndFlush(quizItemAnswer)
            }

        }
    }

    private fun getPollItemType(type: String): PollItemType {
        when (type.toLowerCase()) {
            "multiple-choice" -> {
                return PollItemType.MULTIPLE_CHOICE
            }
            "open-text" -> {
                return PollItemType.OPEN_TEXT
            }
            "quiz" -> {
                return PollItemType.QUIZ
            }
        }
        throw Exception("Item type not allowed")
    }
}