package de.livepoll.api.entity.db

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user")
data class User(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "user_id")
        val id: Int,

        @Column(name = "username", unique = true)
        var username: String,

        @Column(unique = true)
        var email: String,

        var password: String,

        var accountStatus: Boolean,

        var roles: String,

        @JsonIgnore
        @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
        var polls: List<Poll>
) {

    fun getRoleList(): List<String> {
        return if (roles.isNotEmpty()) {
            roles.split(",")
        } else {
            return ArrayList<String>()
        }
    }

}
