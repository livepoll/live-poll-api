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
import org.springframework.transaction.annotation.Transactional
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
    private val websocketPrefix = "/v1/websocket"

    /**
     * Send the current item from a poll to all participants.
     *
     * @param slug the url slug for the websocket url
     * @param pollId the id of the poll to which the current item belongs
     * @param currentItemId the id of the current item that should be send
     */
    fun sendCurrentItem(slug: String, pollId: Long, currentItemId: Long?) {
        val url = "${websocketPrefix}/poll/$slug"
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
                messagingTemplate.convertAndSendToUser(it.name, url, "{\"pollId\":$pollId}")
            }
        }
    }

    /**
     * Save a answer that is send from a participant.
     *
     * @param pollItemId the id of the poll item for which this answer is intended
     * @param payload a json string that contains the answer item
     */
    fun saveAnswer(pollItemId: Long, payload: String) {
        val mapper = ObjectMapper()
        when (mapper.readValue(payload, Map::class.java)["type"].toString()) {
            // Multiple Choice
            PollItemType.MULTIPLE_CHOICE.representation -> {
                val obj: MultipleChoiceItemParticipantAnswerDtoIn =
                    mapper.readValue(payload, MultipleChoiceItemParticipantAnswerDtoIn::class.java)
                val multipleChoiceItemAnswer = multipleChoiceItemAnswerRepository.getById(obj.id)
                multipleChoiceItemAnswer.answerCount++
                multipleChoiceItemAnswerRepository.saveAndFlush(multipleChoiceItemAnswer)
            }

            // Open text
            PollItemType.OPEN_TEXT.representation -> {
                val obj: OpenTextItemParticipantAnswerDtoIn =
                    mapper.readValue(payload, OpenTextItemParticipantAnswerDtoIn::class.java)
                val pollItem = openTextItemRepository.getById(pollItemId)
                openTextItemAnswerRepository.saveAndFlush(obj.toDbEntity(pollItem))
            }

            // Quiz
            PollItemType.QUIZ.representation -> {
                val obj: QuizItemParticipantAnswerDtoIn =
                    mapper.readValue(payload, QuizItemParticipantAnswerDtoIn::class.java)
                val quizItemAnswer = quizItemAnswerRepository.getById(obj.id)
                quizItemAnswer.answerCount++
                quizItemAnswerRepository.saveAndFlush(quizItemAnswer)
            }
        }
    }

    /**
     * Send an item along with its answers to the presenter.
     *
     * @param itemId the id of the item that should be send to the belonging poll presentation endpoint
     */
    @Transactional
    fun sendItemWithAnswers(itemId: Long) {
        val item: PollItemDtoOut = pollItemService.getPollItem(itemId)
        val url = "$websocketPrefix/presentation/${item.pollId}"
        simpUserRegistry.users.forEach {
            messagingTemplate.convertAndSendToUser(it.name, url, item)
        }
    }

}
