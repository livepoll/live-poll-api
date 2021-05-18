package de.livepoll.api.authentication

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "blocked_token")
data class BlockedToken(
        @Id
        @Column(nullable = false)
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,

        @Column(nullable = false)
        val token: String,

        @Column(nullable = false)
        val expiryDate: Date
)