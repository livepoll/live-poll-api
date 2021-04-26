package de.livepoll.api.entity.dto

data class OpenTextItemDtoIn(
    val pollId: Long,
    val question: String,
    val position: Int
)