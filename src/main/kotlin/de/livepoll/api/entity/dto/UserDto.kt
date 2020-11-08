package de.livepoll.api.entity.dto

import com.sun.istack.NotNull

data class UserDto(

        @NotNull
        var username:String,

        @NotNull
        var email:String,

        @NotNull
        var password: String

)