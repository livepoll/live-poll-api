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

    /**
     * Get a single poll item.
     *
     * @param pollItemId the id of the poll item
     * @return the poll item in dto format
     */
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

    /**
     * Delete a single poll item.
     *
     * @param itemId the id of the poll item which should be deleted
     */
    fun deleteItem(itemId: Long) {
        pollItemRepository.findById(itemId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Poll item not found")
        }.run {
            val poll = this.poll
            if (poll.currentItem == itemId) {
                poll.currentItem = null
            }
        }
        pollItemRepository.deleteById(itemId)
    }


    //-------------------------------------------- Create --------------------------------------------------------------

    /**
     * Create a multiple choice item.
     *
     * @param item the new multiple choice item in dto format
     * @return the created multiplce choice item in dto format
     */
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
                    // Insert new item at the end of the poll items (position is counted from 1 onwards)
                    this.pollItems.size + 1,
                    item.question,
                    item.allowMultipleAnswers,
                    item.allowBlankField,
                    mutableListOf()
                )
                // Multiple choice item answers
                multipleChoiceItem.answers =
                    item.selectionOptions.map { MultipleChoiceItemAnswer(0, multipleChoiceItem, it, 0) }.toMutableList()
                multipleChoiceItemRepository.saveAndFlush(multipleChoiceItem)
                multipleChoiceItem.answers.forEach { multipleChoiceItemAnswerRepository.saveAndFlush(it) }
                return multipleChoiceItem.toDtoOut()
            }
    }

    /**
     * Create a quiz item.
     *
     * @param item the new quiz item in dto format
     * @return the created quiz item in dto format
     */
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
                    // Insert new item at the end of the poll items (position is counted from 1 onwards)
                    this.pollItems.size + 1,
                    item.question,
                    mutableListOf()
                )
                // Quiz item answers
                quizItem.answers = item.selectionOptions.mapIndexed { index, element ->
                    QuizItemAnswer(0, quizItem, element, index == 0, 0)
                }.toMutableList()

                quizItemRepository.saveAndFlush(quizItem)
                quizItem.answers.forEach { quizItemAnswerRepository.saveAndFlush(it) }
                return quizItem.toDtoOut()
            }
    }

    /**
     * Create an open text item.
     *
     * @param item the new open text item in dto format
     * @return the created open text item in dto format
     */
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
                    // Insert new item at the end of the poll items (position is counted from 1 onwards)
                    this.pollItems.size + 1,
                    emptyList<OpenTextItemAnswer>().toMutableList()
                )
                return openTextItemRepository.saveAndFlush(openTextItem).toDtoOut()
            }
    }


    //-------------------------------------------- Update --------------------------------------------------------------

    /**
     * Move poll item to another position while updating the positions of other poll items in the poll.
     *
     * This method works in-place and will adjust the poll items list.<br>
     * Old and new position are counted from 1 onwards, NOT from 0!
     *
     * @param oldPos the old item position
     * @param newPos the new item position
     * @param pollItems a list of poll items
     */
    fun movePollItem(oldPos: Int, newPos: Int, pollItems: MutableList<PollItem>) {
        // Check if new position is existent
        if (!(newPos >= 1 && newPos <= pollItems.size)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid poll item position")
        }

        // Sort poll items by position instead of id
        pollItems.sortBy { it.position }

        // Update elements in between
        if (oldPos < newPos) {
            for (i in oldPos + 1..newPos) { // For every element in [oldPos+1, newPos]
                pollItems[i - 1].position-- // Decrease position by one
                pollItemRepository.saveAndFlush(pollItems[i - 1])
            }
        } else if (oldPos > newPos) {
            for (i in newPos until oldPos) {  // For every element in [newPos, oldPos-1]
                pollItems[i - 1].position++ // Increase position by one
                pollItemRepository.saveAndFlush(pollItems[i - 1])
            }
        } else { // oldPos == newPos
            // do nothing
        }

        // Update position of explicitly requested poll item
        pollItems[oldPos - 1].position = newPos
        pollItemRepository.saveAndFlush(pollItems[oldPos - 1])
    }

    /**
     * Update the answers of a poll item according to an update list of selection options (strings).
     * This method works in-place and will adjust the poll item answers list. The order is guaranteed.
     *
     * Requirements this algorithm has to fulfill:
     *
     * - Selection option already exists (in an answer in the database)
     * -> Keep the existing one (including the answer count)
     *
     * - Selection option is new
     * -> Add a new answer to this poll item
     *
     * - Selection option existed in the database but not anymore in the update
     * -> Remove the answer that contained this selection option
     */
    fun updateAnswers(item: PollItemAnswerable, selectionOptionsUpdate: List<String>) {
        // --- Example
        // e indicates: "existing"
        // u indicates: "update"
        // selection_option_existing  ["Ae", "Be", "Ce"]
        // selection_option_update    ["Au", "Du", "Bu"]
        // selection_option_result    ["Ae", "Du", "Be"]
        // note that Ce is gone
        // note that Ae/Be are used in favor of Au/Bu since Ae/Be might include answer counts > 0

        // --- Checks
        if (item is QuizItem) {
            // In the updated list of strings, the first selection option is supposed to be the correct one
            // This might lead to marking an item that was correct before as incorrect.
            // This is only allowed if the previously correct item has an answer count of 0.
            // Otherwise we throw an error.
            if (item.answers.isNotEmpty() && selectionOptionsUpdate.isNotEmpty()) {
                if (item.answers[0].answerCount != 0) {
                    if (item.answers[0].selectionOption != selectionOptionsUpdate[0]) {
                        val msg =
                            "This update would mark an existing correct selection option whose answer count is " +
                                    "greater than 0 as incorrect. Aborting the update."
                        throw ResponseStatusException(HttpStatus.BAD_REQUEST, msg)
                    }
                }
            }
        }

        // --- Build hash map for answers
        val existingAnswers: HashMap<String, SelectionOptionAnswer> = HashMap()
        for (answer in item.answers) {
            // if we deal with a quiz item:
            // make sure that correct options are all false here (they get updated later)
            if (item is QuizItem) {
                (answer as QuizItemAnswer).isCorrect = false
            }

            existingAnswers[answer.selectionOption] = answer
        }

        // --- Build updated answer list
        val answersUpdated: MutableList<SelectionOptionAnswer> = mutableListOf()
        for (selectionOption in selectionOptionsUpdate) {
            if (existingAnswers.containsKey(selectionOption)) {
                // Take the existing answer
                answersUpdated.add(existingAnswers[selectionOption]!!)
            } else {
                // Construct a new answer (depending on the item type)
                when (item) {
                    is MultipleChoiceItem -> {
                        answersUpdated.add(MultipleChoiceItemAnswer(0, item, selectionOption, 0))
                    }
                    is QuizItem -> {
                        // the correct item is updated later
                        answersUpdated.add(QuizItemAnswer(0, item, selectionOption, false, 0))
                    }
                    else -> {
                        // Should never happen
                        throw ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Only multiple choice and quiz items allow for answers"
                        )
                    }
                }
            }
        }

        // --- Quiz item: First item should be correct one
        if (answersUpdated.size > 0) {
            if (item is QuizItem) {
                (answersUpdated[0] as QuizItemAnswer).isCorrect = true
            }
        }

        // --- Override poll item answer list
        item.answers.clear()
        // Don't know why Kotlin wants a Collection<Nothing> here. Might be a bug in Kotlin.
        // If you know a better approach, please fix this.
        item.answers.addAll(answersUpdated as Collection<Nothing>)
    }

    /**
     * Update a multiple choice item.
     *
     * @param pollItemId the id of the item that should be updated
     * @param pollItem the new item in dto format
     * @return the updated item
     */
    fun updateMultipleChoiceItem(
        pollItemId: Long,
        pollItem: MultipleChoiceItemWithPositionDtoIn
    ): MultipleChoiceItemDtoOut {
        multipleChoiceItemRepository.findById(pollItemId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Poll item not found") }
            .run {
                updateAnswers(this, pollItem.selectionOptions)
                this.question = pollItem.question
                this.allowMultipleAnswers = pollItem.allowMultipleAnswers
                this.allowBlankField = pollItem.allowBlankField
                movePollItem(this.position, pollItem.position, this.poll.pollItems)
                pollRepository.saveAndFlush(this.poll)

                return pollItemRepository.saveAndFlush(this).toDtoOut()
            }
    }

    /**
     * Update a quiz item.
     *
     * @param pollItemId the id of the item that should be updated
     * @param pollItem the new item in dto format
     * @return the updated item
     */
    fun updateQuizItem(pollItemId: Long, pollItem: QuizItemWithPositionDtoIn): QuizItemDtoOut {
        if (pollItem.selectionOptions.size < 2) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Quiz item must include at least two items (correct selection option and at least one wrong selection option)"
            )
        }

        quizItemRepository.findById(pollItemId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Poll item not found") }
            .run {
                updateAnswers(this, pollItem.selectionOptions)
                this.question = pollItem.question
                movePollItem(this.position, pollItem.position, this.poll.pollItems)
                pollRepository.saveAndFlush(this.poll)
                return quizItemRepository.saveAndFlush(this).toDtoOut()
            }
    }

    /**
     * Update an open text item.
     *
     * @param pollItemId the id of the item that should be updated
     * @param pollItem the new item in dto format
     * @return the updated item
     */
    fun updateOpenTextItem(pollItemId: Long, pollItem: OpenTextItemWithPositionDtoIn): OpenTextItemDtoOut {
        openTextItemRepository.findById(pollItemId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Poll item not found") }
            .run {
                if (this.answers.isNotEmpty()) {
                    throw ResponseStatusException(HttpStatus.CONFLICT, "This item can not be updated anymore")
                }
                this.question = pollItem.question
                movePollItem(this.position, pollItem.position, this.poll.pollItems)
                pollRepository.saveAndFlush(this.poll)

                return openTextItemRepository.saveAndFlush(this).toDtoOut()
            }
    }

}
