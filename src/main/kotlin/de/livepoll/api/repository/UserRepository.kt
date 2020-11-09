package de.livepoll.api.repository

import de.livepoll.api.entity.db.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Int> {

   @Query("SELECT u FROM User u WHERE u.username = ?1")
   fun findByUsername(username: String): User

   @Query("SELECT COUNT(u.id) FROM User u WHERE u.username = ?1")
   fun countUsersWithUsername(username: String): Int

   @Query("SELECT COUNT(u.id) FROM User u WHERE u.email = ?1")
   fun countUsersWithEmail(email: String): Int
}