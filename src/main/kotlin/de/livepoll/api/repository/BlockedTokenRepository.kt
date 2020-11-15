package de.livepoll.api.repository

import de.livepoll.api.entity.db.BlockedToken
import org.springframework.data.jpa.repository.JpaRepository

interface BlockedTokenRepository : JpaRepository<BlockedToken, Int> {

    fun findByToken(token: String?): BlockedToken?
}