package de.livepoll.api.entity.dto

data class MultipleChoiceItemDtoIn(
        val pollId: Int,
        val question: String,
        val position: Int,
        val answers: List<String>
)