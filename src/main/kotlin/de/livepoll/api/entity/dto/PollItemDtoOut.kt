package de.livepoll.api.entity.dto

open class PollItemDtoOut(
        val itemId: Int,
        val pollId: Int,
        val question: String,
        val position: Int,
        val type: String
)