package de.livepoll.api.pollitem.quiz

class QuizItemAnswerDtoOut(
    val id: Long,
    val selectionOption: String,
    val isCorrect: Boolean,
    val answerCount: Int
)