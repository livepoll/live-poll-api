package de.livepoll.api.entity.db

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "verification_token")
data class VerificationToken(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="verification_token_id")
        val id: Int,

        @Column(name="token")
        val token: String,

        @Column(name="username")
        val username: String,

        @Column(name="expiry_date")
        var expiryDate: Date
)