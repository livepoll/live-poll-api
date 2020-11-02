package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name= "User_Attr")
data class UserAttr(
        @Id
        @NotNull
        var id: Int,

        @NotNull
        var userId: Int,

        @NotNull
        var key1: String,

        @NotNull
        var value: String
){

}