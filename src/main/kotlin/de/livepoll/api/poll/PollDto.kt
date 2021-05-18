package de.livepoll.api.poll

import java.util.*

data class PollDtoIn(
    val name: String,
    val startDate: Date?,
    val endDate: Date?,
    val slug: String?,
    val currentItem: Long?
)

data class PollDtoOut(
    val id: Long,
    val name: String,
    val startDate: Date?,
    val endDate: Date?,
    val slug: String,
    var currentItem: Long?
)
