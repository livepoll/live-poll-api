package de.livepoll.api.entity.dto

data class MultipleChoiceItemDtoIn(
        val pollId: Long,
        val question: String,
        val allowMultipleAnswers: Boolean,
        val allowBlankField: Boolean,
        val answers: List<String>,
)

data class MultipleChoiceItemWithPositionDtoIn(
        val pollId: Long,
        val question: String,
        val position: Int, // extra field
        val allowMultipleAnswers: Boolean,
        val allowBlankField: Boolean,
        val answers: List<String>,
)
