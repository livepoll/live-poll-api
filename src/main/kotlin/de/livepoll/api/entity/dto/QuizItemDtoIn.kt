package de.livepoll.api.entity.dto

class QuizItemDtoIn(
    var pollId: Long,
    val question: String,
    val answers: List<String>
)

class QuizItemWithPositionDtoIn(
    var pollId: Long,
    val position: Int,
    val question: String,
    val answers: List<String>
)
