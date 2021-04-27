package de.livepoll.api.service

import com.fasterxml.jackson.databind.ObjectMapper
import de.livepoll.api.entity.db.*
import de.livepoll.api.entity.dto.PollItemDtoOut
import de.livepoll.api.repository.MultipleChoiceItemAnswerRepository
import de.livepoll.api.repository.MultipleChoiceItemRepository
import de.livepoll.api.repository.OpenTextItemAnswerRepository
import de.livepoll.api.repository.QuizItemAnswerRepository
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Controller


@Controller
class WebSocketService(
        private val messagingTemplate: SimpMessageSendingOperations,
        private val pollItemService: PollItemService,
        private val multipleChoiceItemAnswerRepository: MultipleChoiceItemAnswerRepository,
        private val openTextItemAnswerRepository: OpenTextItemAnswerRepository,
        private val quizItemAnswerRepository: QuizItemAnswerRepository,
        private val multipleChoiceItemRepository: MultipleChoiceItemRepository
) {
    fun sendCurrenItem(slug: String, currentItemId: Long) {
        val item: PollItemDtoOut = pollItemService.getPollItem(currentItemId);
        val url = "/poll/" + slug
        messagingTemplate.convertAndSend(url, item)
    }


    fun saveAnswer(pollItemId: Long, payload: String) {
        val mapper = ObjectMapper()
        val map: Map<String, String> = mapper.readValue(payload, Map::class.java) as Map<String, String>
        when (getPollItemType(map.get("type")!!)) {

            // Multiple Choice
            PollItemType.MULTIPLE_CHOICE -> {
                val pollItem: MultipleChoiceItem = multipleChoiceItemRepository.getOne(pollItemId)
                var obj: MultipleChoiceItemAnswer = mapper.readValue(payload, MultipleChoiceItemAnswer::class.java)
                println(obj.id)
                println(obj.answerCount)
                println(obj.multipleChoiceItem.question)
                println(obj.selectionOption)
                pollItem.answers.add(obj)
            }

            // Open text
            PollItemType.OPEN_TEXT -> {
                val obj: OpenTextItemAnswer = mapper.readValue(payload, OpenTextItemAnswer::class.java)
                openTextItemAnswerRepository.saveAndFlush(obj)
            }

            // Quiz
            PollItemType.QUIZ -> {
                val obj: QuizItemAnswer = mapper.readValue(payload, QuizItemAnswer::class.java)
                quizItemAnswerRepository.saveAndFlush(obj)
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