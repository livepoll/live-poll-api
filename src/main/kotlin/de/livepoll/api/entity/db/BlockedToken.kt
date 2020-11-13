package de.livepoll.api.entity.db

import java.util.Date
import javax.persistence.*

@Entity
@Table(name = "blocked_token")
data class BlockedToken(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,
        val token: String,
        val expiryDate: Date
)