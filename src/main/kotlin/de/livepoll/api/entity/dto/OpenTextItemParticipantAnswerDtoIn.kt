package de.livepoll.api.entity.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class OpenTextItemParticipantAnswerDtoIn(
    @JsonProperty("id")
    val id: Long,
    @JsonProperty("type")
    val type: String,
    @JsonProperty("answer")
    val answer: String
)
