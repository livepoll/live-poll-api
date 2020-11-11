package de.livepoll.api.service

import de.livepoll.api.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class JwtUserDetailsService: UserDetailsService {

    @Autowired
    private lateinit var userRepository: UserRepository

    override fun loadUserByUsername(username: String): UserDetails {
        var user = userRepository.findByUsername(username)
        user?.run { return User(this.username, this.password, ArrayList()) }
        user = userRepository.findByEmail(username)
        user?.run { return User(this.username, this.password, ArrayList()) }
        throw UsernameNotFoundException("User does not exist")
    }
}