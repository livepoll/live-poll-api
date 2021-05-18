package de.livepoll.api.entity.dto

data class OpenTextItemDtoIn(
    val pollId: Long,
    val question: String,
)

data class OpenTextItemWithPositionDtoIn(
    val pollId: Long,
    val position: Int, // extra field
    val question: String,
)
