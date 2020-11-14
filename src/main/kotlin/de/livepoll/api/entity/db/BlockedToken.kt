package de.livepoll.api.entity.db

import java.util.Date
import javax.persistence.*

@Entity
@Table(name = "blocked_token")
data class BlockedToken(
        @Id
        @Column(nullable = false)
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,

        @Column(nullable = false)
        val token: String,

        @Column(nullable = false)
        val expiryDate: Date
)