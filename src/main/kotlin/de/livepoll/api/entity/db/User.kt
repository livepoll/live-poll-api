package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "User")
open class User(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @NotNull
        var id: Int,

        @NotNull
        private var username: String,

        @NotNull
        var email: String,

        @NotNull
        private var password: String,

        @NotNull
        var accountStatus: Boolean,

        @NotNull
        var roles: String
){
        constructor(user: User){
                id = user.id
                username = user.username
                email = user.email
                password = user.password
                accountStatus = user.accountStatus
                roles = user.roles;
        }

        fun getRoleList(): List<String> {
                return if (roles.length > 0){
                        roles.split(",")
                }else{
                        return ArrayList<String>()
                }
        }

        open fun getUsername(): String{
                return username
        }

        open fun getPassword(): String{
                return password
        }

}