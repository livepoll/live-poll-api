package de.livepoll.api.entity.dto

data class MultipleChoiceItemDtoIn(
        var pollId: Int,
        val question: String,
        val position: Int,
        val answers: List<String>
)