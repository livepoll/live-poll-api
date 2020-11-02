package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "User")
open class User(

        @Id
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
        var roles: String,

        @NotNull
        @OneToMany(mappedBy = "userId")
        val polls: List<Poll>
){
        constructor(user: User): this(user.id, user.username, user.email, user.password, user.accountStatus, user.roles, user.polls){
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