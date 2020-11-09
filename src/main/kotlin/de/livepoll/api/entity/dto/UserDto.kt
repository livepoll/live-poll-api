package de.livepoll.api.entity.dto

import org.jetbrains.annotations.NotNull

data class UserDto(
        @NotNull
        var id:Int,

        @NotNull
        var name:String,

        @NotNull
        var email:String,

        @NotNull
        var password: String

)