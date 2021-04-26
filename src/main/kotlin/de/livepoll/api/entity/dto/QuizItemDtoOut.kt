package de.livepoll.api.entity.dto

class QuizItemDtoOut(
    itemId: Long,
    pollId: Long,
    question: String,
    position: Int,
    type: String,
    val answers: List<QuizItemAnswerDtoOut>
) : PollItemDtoOut(itemId, pollId, question, position, type)