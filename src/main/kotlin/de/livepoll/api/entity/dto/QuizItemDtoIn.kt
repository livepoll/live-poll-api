package de.livepoll.api.entity.dto

open class QuizItemDtoIn(
    pollId: Long,
    question: String,
    val selectionOptions: List<String>
) : PollItemDtoIn(pollId, question)

class QuizItemWithPositionDtoIn(
    pollId: Long,
    question: String,
    selectionOptions: List<String>,
    val position: Int
) : QuizItemDtoIn(pollId, question, selectionOptions)
