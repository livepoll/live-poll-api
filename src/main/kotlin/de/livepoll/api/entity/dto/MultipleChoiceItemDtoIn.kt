package de.livepoll.api.entity.dto

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
