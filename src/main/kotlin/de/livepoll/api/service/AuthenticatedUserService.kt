package de.livepoll.api.service

import de.livepoll.api.entity.AuthenticatedUser
import de.livepoll.api.entity.db.User
import de.livepoll.api.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class AuthenticatedUserService: UserDetailsService {

    @Autowired
    lateinit var userRepository: UserRepository

    override fun loadUserByUsername(email: String): UserDetails {
        val user: User = userRepository.findByEmail(email)
        return AuthenticatedUser(user)
    }
}