package de.livepoll.api.service


import com.fasterxml.jackson.databind.ObjectMapper
import de.livepoll.api.entity.db.PollItemType
import de.livepoll.api.entity.dto.MultipleChoiceItemParticipantAnswerDtoIn
import de.livepoll.api.entity.dto.OpenTextItemParticipantAnswerDtoIn
import de.livepoll.api.entity.dto.PollItemDtoOut
import de.livepoll.api.entity.dto.QuizItemParticipantAnswerDtoIn
import de.livepoll.api.repository.MultipleChoiceItemAnswerRepository
import de.livepoll.api.repository.OpenTextItemAnswerRepository
import de.livepoll.api.repository.OpenTextItemRepository
import de.livepoll.api.repository.QuizItemAnswerRepository
import de.livepoll.api.util.toDbEntity
import org.springframework.http.HttpStatus
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.user.SimpUserRegistry
import org.springframework.stereotype.Controller
import org.springframework.web.server.ResponseStatusException


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

    fun sendCurrentItem(slug: String, pollId: Long, currentItemId: Long?) {
        val url = "/v1/websocket/poll/$slug"
        if (currentItemId != null) {
            val item: PollItemDtoOut = pollItemService.getPollItem(currentItemId)
            if (pollId == item.pollId) {
                simpUserRegistry.users.forEach {
                    messagingTemplate.convertAndSendToUser(it.name, url, item)
                }
            } else {
                throw ResponseStatusException(HttpStatus.CONFLICT, "Item is not part of poll")
            }
        } else {
            simpUserRegistry.users.forEach {
                messagingTemplate.convertAndSendToUser(it.name, url, "{\"id\":$pollId}")
            }
        }
    }

    fun saveAnswer(pollItemId: Long, payload: String) {
        println("PAYLOAD: $payload")
        val mapper = ObjectMapper()
        val type: String = mapper.readValue(payload, Map::class.java)["type"].toString()
        when (type) {
            // Multiple Choice
            PollItemType.MULTIPLE_CHOICE.representation -> {
                val obj: MultipleChoiceItemParticipantAnswerDtoIn =
                    mapper.readValue(payload, MultipleChoiceItemParticipantAnswerDtoIn::class.java)
                val multipleChoiceItemAnswer = multipleChoiceItemAnswerRepository.getOne(obj.id)
                multipleChoiceItemAnswer.answerCount++
                multipleChoiceItemAnswerRepository.saveAndFlush(multipleChoiceItemAnswer)
            }

            // Open text
            PollItemType.OPEN_TEXT.representation -> {
                val obj: OpenTextItemParticipantAnswerDtoIn =
                    mapper.readValue(payload, OpenTextItemParticipantAnswerDtoIn::class.java)
                val pollItem = openTextItemRepository.getOne(pollItemId)
                openTextItemAnswerRepository.saveAndFlush(obj.toDbEntity(pollItem))
            }

            // Quiz
            PollItemType.QUIZ.representation -> {
                val obj: QuizItemParticipantAnswerDtoIn =
                    mapper.readValue(payload, QuizItemParticipantAnswerDtoIn::class.java)
                val quizItemAnswer = quizItemAnswerRepository.getOne(obj.id)
                quizItemAnswer.answerCount++
                quizItemAnswerRepository.saveAndFlush(quizItemAnswer)
            }
        }
    }

}
