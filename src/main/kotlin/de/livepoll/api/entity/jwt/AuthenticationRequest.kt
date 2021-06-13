package de.livepoll.api.entity.jwt

data class AuthenticationRequest(
    var username: String,
    var password: String
)
