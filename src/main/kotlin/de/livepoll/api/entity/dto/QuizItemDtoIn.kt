package de.livepoll.api.entity.dto

class QuizItemDtoIn(
        var pollId: Int,
        val question: String,
        val position: Int,
        val answers: List<String>
)