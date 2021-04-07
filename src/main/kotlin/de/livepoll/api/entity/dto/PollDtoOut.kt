package de.livepoll.api.entity.dto

import de.livepoll.api.entity.db.PollItem
import java.util.Date

data class PollDtoOut(
        val id: Int,
        val name: String,
        val startDate: Date,
        val endDate: Date,
        val slug: String
)
