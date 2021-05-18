package de.livepoll.api.user

data class UserDtoIn(
    var username: String,
    var email: String,
    var password: String,
)

data class UserDtoOut(
    var id: Long,
    var username: String,
    var email: String
)
