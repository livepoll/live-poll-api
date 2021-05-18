package de.livepoll.api.authentication

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface VerificationTokenRepository: JpaRepository<VerificationToken, String> {

    @Query("SELECT u FROM VerificationToken u WHERE u.token = ?1")
    fun findByToken(token: String): VerificationToken
}