package de.livepoll.api.pollitem.multiplechoice

import com.fasterxml.jackson.annotation.JsonProperty

data class MultipleChoiceItemParticipantAnswerDtoIn(
        @JsonProperty("id")
        val id: Long,
        @JsonProperty("type")
        val type: String,
        @JsonProperty("selectionOption")
        val selectionOption: String
)