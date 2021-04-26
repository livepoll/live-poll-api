package de.livepoll.api.entity.dto

data class MultipleChoiceItemDtoIn(
        val pollId: Long,
        val question: String,
        val position: Int,
        val allowMultipleAnswers: Boolean,
        val allowBlankField: Boolean,
        val answers: List<String>,
)