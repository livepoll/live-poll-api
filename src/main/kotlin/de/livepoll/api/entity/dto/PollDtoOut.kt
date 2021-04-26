package de.livepoll.api.entity.dto

import java.util.*

data class PollDtoOut(
        val id: Int,
        val name: String,
        val startDate: Date,
        val endDate: Date,
        val slug: String,
        var currentItem: Int?
)
