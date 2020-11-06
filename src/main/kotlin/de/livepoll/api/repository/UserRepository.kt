package de.livepoll.api.repository

import de.livepoll.api.entity.db.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Int> {

   @Query("select u from User u where u.username =?1")
   fun findByUsername(username:String):User
}