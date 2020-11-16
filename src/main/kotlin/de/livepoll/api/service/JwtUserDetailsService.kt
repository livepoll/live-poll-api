package de.livepoll.api.service

import de.livepoll.api.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*


@Service
class JwtUserDetailsService: UserDetailsService {

    @Autowired
    private lateinit var userRepository: UserRepository

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username) ?: userRepository.findByEmail(username)
        user?.run {
            val authorities = mutableListOf<GrantedAuthority>()
            user.getRoleList().forEach { authorities.add(SimpleGrantedAuthority(it)) }
            return User(this.username, this.password, authorities) }
        throw UsernameNotFoundException("User does not exist")
    }
}