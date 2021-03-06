package de.livepoll.api.repository

import de.livepoll.api.entity.db.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    // @Query("SELECT u FROM User u WHERE u.username = ?1")
    fun findByUsername(username: String): User?

    fun findByEmail(email: String): User?

    // @Query("SELECT COUNT(u.id) FROM User u WHERE u.username = ?1")
    fun existsByUsername(username: String): Boolean

    // @Query("SELECT COUNT(u.id) FROM User u WHERE u.email = ?1")
    fun existsByEmail(email: String): Boolean
}
