package de.livepoll.api.entity.dto

class QuizItemDtoIn(
    var pollId: Long,
    val question: String,
    val position: Int,
    val answers: List<QuizItemAnswerDtoIn>
)

class QuizItemAnswerDtoIn(
    val answer: String,
    val isCorrect: Boolean
)