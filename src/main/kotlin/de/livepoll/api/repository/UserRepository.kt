package de.livepoll.api.repository

import de.livepoll.api.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Int> {

}

