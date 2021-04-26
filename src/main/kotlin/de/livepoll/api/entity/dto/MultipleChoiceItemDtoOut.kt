package de.livepoll.api.entity.dto

class MultipleChoiceItemDtoOut(
        itemId: Long,
        pollId: Long,
        question: String,
        position: Int,
        type: String,
        val answers: List<MultipleChoiceItemAnswerDtoOut>
): PollItemDtoOut(itemId, pollId, question, position, type)
