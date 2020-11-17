package de.livepoll.api.util

import de.livepoll.api.entity.db.Poll
import de.livepoll.api.entity.db.User
import de.livepoll.api.entity.dto.PollDtoOut
import de.livepoll.api.entity.dto.UserDtoIn
import de.livepoll.api.entity.dto.UserDtoOut

// --------------------------------------------------- Poll mappers ----------------------------------------------------

fun Poll.toDtoOut() = PollDtoOut(this.id, this.name, this.startDate, this.endDate)

// --------------------------------------------------- User mappers ----------------------------------------------------

fun User.toDtoOut() = UserDtoOut(this.id, this.username, this.email, this.polls)

fun UserDtoIn.toDbEntity() = User(0, this.username, this.email, this.password, false, "", emptyList())
