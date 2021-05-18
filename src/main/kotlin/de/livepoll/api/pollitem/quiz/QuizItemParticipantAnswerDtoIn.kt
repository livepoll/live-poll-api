package de.livepoll.api.pollitem.quiz

import com.fasterxml.jackson.annotation.JsonProperty

data class QuizItemParticipantAnswerDtoIn(
        @JsonProperty("id")
        val id: Long,
        @JsonProperty("type")
        val type: String,
        @JsonProperty("selectionOption")
        val selectionOption: String
)