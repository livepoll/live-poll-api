package de.livepoll.api.pollitem.opentext

import com.fasterxml.jackson.annotation.JsonProperty

data class OpenTextItemParticipantAnswerDtoIn(
        @JsonProperty("id")
        val id: Long,
        @JsonProperty("type")
        val type: String,
        @JsonProperty("answer")
        val answer: String
)