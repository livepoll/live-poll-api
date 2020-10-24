package de.livepoll.api.entity

import com.sun.istack.NotNull
import javax.persistence.*

@Entity
@Table(name= "User")
class User(
        @Id
        @NotNull
        var id: Int,

        @NotNull
        var username:String,

        @NotNull
        var email: String,

        @NotNull
        var password: String,

        @NotNull
        var accountStatus: Boolean
){

}