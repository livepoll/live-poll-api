package de.livepoll.api.repository

import de.livepoll.api.entity.db.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Int> {
   fun findByEmail(email: String): User
}