package de.livepoll.api.entity.dto

import java.util.*

data class PollDtoOut(
        val name: String,
        val startDate: Date,
        val endDate: Date
)