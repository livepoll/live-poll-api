package de.livepoll.api.pollitem

open class PollItemDtoIn(
    val pollId: Long,
    val question: String
)

open class PollItemDtoOut(
    val itemId: Long,
    val pollId: Long,
    val question: String,
    val position: Int,
    val type: String
)
