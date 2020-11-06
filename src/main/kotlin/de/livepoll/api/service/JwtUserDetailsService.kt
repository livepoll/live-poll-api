package de.livepoll.api.service

import de.livepoll.api.entity.db.User
import de.livepoll.api.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*


@Service
class JwtUserDetailsService: UserDetailsService {

    @Autowired
    private lateinit var userRepository: UserRepository;

    override fun loadUserByUsername(username: String): UserDetails {
        val user: User = userRepository.findByUsername(username)
        println(user.username)
        return org.springframework.security.core.userdetails.User(user.username, user.password, ArrayList());
    }
}