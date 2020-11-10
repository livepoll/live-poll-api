package de.livepoll.api.entity.dto

import de.livepoll.api.entity.db.Poll

data class UserDtoOut(
        var id: Int,
        var username: String,
        var email: String,
        var password: String,
        var polls: List<Poll>
)
