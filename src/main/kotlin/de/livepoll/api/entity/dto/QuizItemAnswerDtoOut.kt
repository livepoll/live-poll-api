package de.livepoll.api.entity.dto

class QuizItemAnswerDtoOut(
    val id: Long,
    val selectionOption: String,
    val isCorrect: Boolean,
    val answerCount: Int
)