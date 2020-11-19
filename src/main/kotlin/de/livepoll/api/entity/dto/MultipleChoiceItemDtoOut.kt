package de.livepoll.api.entity.dto

import de.livepoll.api.entity.db.Answer

data class MultipleChoiceItemDtoOut(
        val itemId: Int,
        val pollId: Int,
        val question: String,
        val answers: List<Answer>,
        val position: Int
)