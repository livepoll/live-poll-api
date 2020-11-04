package de.livepoll.api.entity.db

import com.sun.istack.NotNull
import org.hibernate.type.descriptor.sql.TinyIntTypeDescriptor
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "users")
open class User(

        @Id
        @NotNull
        @GeneratedValue(strategy=GenerationType.IDENTITY)
        @Column(name="user_id")
        var id: Int,

        @NotNull
        var username: String,

        @NotNull
        var email: String,

        @NotNull
        var password: String,

        @NotNull
        var accountStatus: Boolean,

        @NotNull
        var roles: String,

        @NotNull
        var enabled: TinyIntTypeDescriptor,

        @NotNull
        @OneToMany(mappedBy = "user")
        var polls: List<Poll>
){


        fun getRoleList(): List<String> {
                return if (roles.length > 0){
                        roles.split(",")
                }else{
                        return ArrayList<String>()
                }
        }



}