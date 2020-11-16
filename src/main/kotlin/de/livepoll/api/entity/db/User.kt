package de.livepoll.api.entity.db

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "user")
data class User(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "user_id", nullable = false)
        val id: Int,

        @Column(name = "username", unique = true, nullable = false)
        var username: String,

        @Column(unique = true, nullable = false)
        var email: String,

        @Column(nullable = false)
        var password: String,

        @Column(nullable = false)
        var accountStatus: Boolean,

        @Column(nullable = false)
        var roles: String,

        @JsonIgnore
        @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
        var polls: List<Poll>
) {
    fun getRoleList() = if (roles.isNotEmpty()) roles.split(",") else emptyList()
}
