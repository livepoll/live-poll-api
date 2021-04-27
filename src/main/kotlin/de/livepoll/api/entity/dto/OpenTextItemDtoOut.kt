package de.livepoll.api.entity.dto

class OpenTextItemDtoOut(
        itemId: Long,
        pollId: Long,
        question: String,
        position: Int,
        type: String,
        val answers: List<OpenTextItemAnswerDtoOut>
): PollItemDtoOut(itemId, pollId, question, position, type)
