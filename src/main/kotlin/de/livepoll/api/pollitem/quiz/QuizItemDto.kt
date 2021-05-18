package de.livepoll.api.pollitem.quiz

import de.livepoll.api.pollitem.PollItemDtoIn
import de.livepoll.api.pollitem.PollItemDtoOut

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

class QuizItemDtoOut(
    itemId: Long,
    pollId: Long,
    question: String,
    position: Int,
    type: String,
    val answers: List<QuizItemAnswerDtoOut>
) : PollItemDtoOut(itemId, pollId, question, position, type)
