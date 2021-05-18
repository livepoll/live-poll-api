package de.livepoll.api.entity.dto

open class QuizItemDtoIn(
    pollId: Long,
    question: String,
    val answers: List<String>
) : PollItemDtoIn(pollId, question)

class QuizItemWithPositionDtoIn(
    pollId: Long,
    question: String,
    answers: List<String>,
    val position: Int
) : QuizItemDtoIn(pollId, question, answers)
