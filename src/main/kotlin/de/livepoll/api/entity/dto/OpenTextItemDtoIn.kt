package de.livepoll.api.entity.dto

open class OpenTextItemDtoIn(
    pollId: Long,
    question: String,
) : PollItemDtoIn(pollId, question)

class OpenTextItemWithPositionDtoIn(
    pollId: Long,
    question: String,
    val position: Int
) : OpenTextItemDtoIn(pollId, question)
