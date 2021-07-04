package de.livepoll.api.entity.dto

class PollItemWithPollNameDtoOut(
        val itemId: Long,
        val pollId: Long,
        val question: String,
        val position: Int,
        val type: String,
        val pollName: String
)