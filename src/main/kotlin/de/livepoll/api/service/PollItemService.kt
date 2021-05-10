package de.livepoll.api.service

import de.livepoll.api.entity.db.*
import de.livepoll.api.entity.dto.*
import de.livepoll.api.repository.*
import de.livepoll.api.util.toDtoOut
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import javax.sql.rowset.Predicate
import javax.swing.text.MutableAttributeSet

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
                return when (type) {
                    // Multiple Choice
                    PollItemType.MULTIPLE_CHOICE -> multipleChoiceItemRepository.findById(pollItemId)
                        .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Multiple choice item not found") }
                        .toDtoOut()

                    // Open Text
                    PollItemType.OPEN_TEXT -> openTextItemRepository.findById(pollItemId)
                        .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, ("Open text item not found")) }
                        .toDtoOut()

                    // Quiz
                    PollItemType.QUIZ -> quizItemRepository.findById(pollItemId)
                        .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz item not found") }
                        .toDtoOut()
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
                quizItem.answers = item.answers.mapIndexed { index, element ->
                    QuizItemAnswer(0, quizItem, element, index == 0, 0)
                }.toMutableList()

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


    //-------------------------------------------- Update --------------------------------------------------------------

    fun updateMultipleChoiceItem(pollItemId: Long, pollItem: MultipleChoiceItemDtoIn): MultipleChoiceItemDtoOut {
        multipleChoiceItemRepository.findById(pollItemId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Poll item not found") }
            .run {
                val newAnswers = pollItem.answers.toMutableList()
                val removeAnswer = mutableListOf<MultipleChoiceItemAnswer>()
                this.answers.forEach {
                    if (it.answerCount != 0) {
                        if(pollItem.answers.contains(it.selectionOption)){
                            newAnswers.remove(it.selectionOption)
                        }else{
                            this.answers.remove(it)
                        }
                    }else{
                        if(!pollItem.answers.contains(it.selectionOption)){
                            removeAnswer.add(it)
                        }
                    }
                }
                removeAnswer.forEach {
                    this.answers.remove(it)
                }
                newAnswers.forEach {
                    this.answers.add(MultipleChoiceItemAnswer(0, this, it, 0))
                }

                this.question = pollItem.question
                this.allowMultipleAnswers = pollItem.allowMultipleAnswers
                this.allowBlankField = pollItem.allowBlankField
                this.position = pollItem.position

                return pollItemRepository.saveAndFlush(this).toDtoOut()
            }
    }

    fun updateQuizItem(pollItemId: Long, pollItem: QuizItemDtoIn): QuizItemDtoOut {
        quizItemRepository.findById(pollItemId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Poll item not found") }
            .run {
                val newAnswers = pollItem.answers.toMutableList()
                val removeAnswer = mutableListOf<QuizItemAnswer>()
                this.answers.forEach {
                    if (it.answerCount != 0) {
                        if(pollItem.answers.contains(it.selectionOption)){
                            newAnswers.remove(it.selectionOption)
                            it.isCorrect = false
                        }else{
                            this.answers.remove(it)
                        }
                    }else{
                        if(!pollItem.answers.contains(it.selectionOption)){
                            removeAnswer.add(it)
                        }
                    }
                }
                removeAnswer.forEach {
                    this.answers.remove(it)
                }
                newAnswers.forEach {
                    this.answers.add(QuizItemAnswer(0, this, it, false, 0))
                }
                val newCorrectOne = this.answers.find{it.selectionOption == pollItem.answers[0]}!!
                if(newCorrectOne.answerCount == 0){
                    this.answers.find{it.selectionOption == pollItem.answers[0]}!!.isCorrect = true
                }

                this.question = pollItem.question
                this.position = pollItem.position

                return quizItemRepository.saveAndFlush(this).toDtoOut()
            }
    }

    fun updateOpenTextItem(pollItemId: Long, pollItem: OpenTextItemDtoIn): OpenTextItemDtoOut {
        openTextItemRepository.findById(pollItemId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Poll item not found") }
            .run {
                if (!this.answers.isEmpty()) {
                    throw ResponseStatusException(HttpStatus.CONFLICT, "This item can not be updated anymore")
                }
                this.question = pollItem.question
                this.position = pollItem.position
                return openTextItemRepository.saveAndFlush(this).toDtoOut()
            }
    }

}
