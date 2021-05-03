package de.livepoll.api.entity.dto

data class OpenTextItemDtoIn(
    val pollId: Long,
    val position: Int,
    val question: String,
)
