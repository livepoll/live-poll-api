package de.livepoll.api.service

import de.livepoll.api.entity.db.*
import de.livepoll.api.entity.dto.*
import de.livepoll.api.repository.*
import de.livepoll.api.util.toDtoOut
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class PollItemService {
    @Autowired
    private lateinit var pollRepository: PollRepository

    @Autowired
    private lateinit var pollItemRepository: PollItemRepository<PollItem>

    @Autowired
    private lateinit var multipleChoiceItemRepository: MultipleChoiceItemRepository

    @Autowired
    private lateinit var multipleChoiceItemAnswerRepository: MultipleChoiceItemAnswerRepository

    @Autowired
    private lateinit var openTextItemRepository: OpenTextItemRepository

    @Autowired
    private lateinit var quizItemRepository: QuizItemRepository

    @Autowired
    private lateinit var quizItemAnswerRepository: QuizItemAnswerRepository

    //--------------------------------------------- Get ----------------------------------------------------------------

    fun getPollItem(pollItemId: Long): PollItemDtoOut {
        pollItemRepository.findById(pollItemId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Poll item not found") }
            .run {
                when (this.type) {

                    // Multiple Choice
                    PollItemType.MULTIPLE_CHOICE -> {
                        return multipleChoiceItemRepository.findById(pollItemId)
                            .orElseThrow {
                                ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Multiple choice item not found"
                                )
                            }
                            .run { this.toDtoOut() }
                    }

                    // Open text
                    PollItemType.OPEN_TEXT -> {
                        return openTextItemRepository.findById(pollItemId)
                            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, ("Open text item not found")) }
                            .run { this.toDtoOut() }
                    }

                    // Quiz
                    PollItemType.QUIZ -> {
                        return quizItemRepository.findById(pollItemId)
                            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz item not found") }
                            .run { this.toDtoOut() }
                    }

                }
            }
    }

    fun deleteItem(itemId: Long) {
        pollItemRepository.deleteById(itemId)
    }


    //-------------------------------------------- Create --------------------------------------------------------------

    fun createMultipleChoiceItem(item: MultipleChoiceItemDtoIn): MultipleChoiceItemDtoOut {
        pollRepository.findById(item.pollId)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.NO_CONTENT,
                    "The corresponding poll for this multiple choice item could not be retrieved"
                )
            }.run {
                // Multiple choice item
                val multipleChoiceItem = MultipleChoiceItem(
                    0,
                    this,
                    item.position,
                    item.question,
                    item.allowMultipleAnswers,
                    item.allowBlankField,
                    mutableListOf()
                )
                // Multiple choice item answers
                multipleChoiceItem.answers =
                    item.answers.map { MultipleChoiceItemAnswer(0, multipleChoiceItem, it, 0) }.toMutableList()
                multipleChoiceItemRepository.saveAndFlush(multipleChoiceItem)
                multipleChoiceItem.answers.forEach { multipleChoiceItemAnswerRepository.saveAndFlush(it) }
                return multipleChoiceItem.toDtoOut()
            }
    }

    fun createQuizItem(item: QuizItemDtoIn): QuizItemDtoOut {
        pollRepository.findById(item.pollId)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.NO_CONTENT,
                    "The corresponding poll for this quiz item could not be retrieved"
                )
            }.run {
                // Quiz item
                val quizItem = QuizItem(
                    0,
                    this,
                    item.position,
                    item.question,
                    mutableListOf()
                )
                // Quiz item answers
                quizItem.answers =
                    item.answers.map { QuizItemAnswer(0, quizItem, it.answer, it.isCorrect, 0) }.toMutableList()
                quizItemRepository.saveAndFlush(quizItem)
                quizItem.answers.forEach { quizItemAnswerRepository.saveAndFlush(it) }
                return quizItem.toDtoOut()
            }
    }

    fun createOpenTextItem(item: OpenTextItemDtoIn): OpenTextItemDtoOut {
        pollRepository.findById(item.pollId)
            .orElseThrow {
                ResponseStatusException(
                    HttpStatus.NO_CONTENT,
                    "The corresponding poll for this open text item could not be retrieved"
                )
            }.run {
                val openTextItem = OpenTextItem(
                    0,
                    this,
                    item.question,
                    item.position,
                    emptyList<OpenTextItemAnswer>().toMutableList()
                )
                return openTextItemRepository.saveAndFlush(openTextItem).toDtoOut()
            }
    }

}
