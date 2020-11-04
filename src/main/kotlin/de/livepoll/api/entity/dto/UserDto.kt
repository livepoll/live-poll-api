package de.livepoll.api.entity.dto

import com.sun.istack.NotNull

class UserDto(
        @NotNull
        var id:Int,

        @NotNull
        var name:String,

        @NotNull
        var email:String,

        @NotNull
        var password: String

) {

}