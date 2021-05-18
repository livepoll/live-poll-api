package de.livepoll.api.pollitem.multiplechoice

import de.livepoll.api.pollitem.PollItemDtoIn
import de.livepoll.api.pollitem.PollItemDtoOut

open class MultipleChoiceItemDtoIn(
    pollId: Long,
    question: String,
    val allowMultipleAnswers: Boolean,
    val allowBlankField: Boolean,
    val answers: List<String>,
) : PollItemDtoIn(pollId, question)

class MultipleChoiceItemWithPositionDtoIn(
    pollId: Long,
    question: String,
    allowMultipleAnswers: Boolean,
    allowBlankField: Boolean,
    answers: List<String>,
    val position: Int
) : MultipleChoiceItemDtoIn(pollId, question, allowMultipleAnswers, allowBlankField, answers)

class MultipleChoiceItemDtoOut(
    itemId: Long,
    pollId: Long,
    question: String,
    position: Int,
    type: String,
    val answers: List<MultipleChoiceItemAnswerDtoOut>
) : PollItemDtoOut(itemId, pollId, question, position, type)
