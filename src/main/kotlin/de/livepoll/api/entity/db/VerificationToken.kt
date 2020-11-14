package de.livepoll.api.entity.db

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "verification_token")
data class VerificationToken(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="verification_token_id", nullable = false)
        val id: Int,

        @Column(name="token", nullable = false)
        val token: String,

        @Column(name="username", nullable = false)
        val username: String,

        @Column(name="expiry_date", nullable = false)
        var expiryDate: Date
)