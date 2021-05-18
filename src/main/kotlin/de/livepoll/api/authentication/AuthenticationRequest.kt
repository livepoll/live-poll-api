package de.livepoll.api.authentication

data class AuthenticationRequest(
        var username: String,
        var password: String
)