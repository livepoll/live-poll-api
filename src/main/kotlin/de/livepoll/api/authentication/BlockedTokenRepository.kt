package de.livepoll.api.authentication

import org.springframework.data.jpa.repository.JpaRepository

interface BlockedTokenRepository : JpaRepository<BlockedToken, Int> {

    fun findByToken(token: String?): BlockedToken?
}