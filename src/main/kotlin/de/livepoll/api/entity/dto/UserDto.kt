package de.livepoll.api.entity.dto

data class UserDto(
        var id: Int?,
        var username: String,
        var email: String,
        var password: String
)