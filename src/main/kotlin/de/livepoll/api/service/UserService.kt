package de.livepoll.api.service

import de.livepoll.api.entity.db.User
import de.livepoll.api.entity.dto.UserDtoIn
import de.livepoll.api.entity.dto.UserDtoOut

class UserService {

}

fun User.toDtoOut(): UserDtoOut {
    return UserDtoOut(0, this.username, this.email, this.password, this.polls)
}

fun UserDtoIn.toDbEntity(): User {
    return User(0, this.username, this.email, this.password, false, "", emptyList())
}
