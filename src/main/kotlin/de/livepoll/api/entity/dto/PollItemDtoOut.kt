package de.livepoll.api.entity.dto

open class PollItemDtoOut(
    val itemId: Long,
    val pollId: Long,
    val question: String,
    val position: Int,
    val type: String
)
