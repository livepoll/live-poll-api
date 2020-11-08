package de.livepoll.api.service

import de.livepoll.api.entity.db.User
import de.livepoll.api.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.RestController

@RestController
class UserService{

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var userRepository: UserRepository

    fun createAccount(user: User): User {
        user.password = passwordEncoder.encode(user.password)
        user.enabled = false
        user.roles="ROLE_USER"
        userRepository.saveAndFlush(user)
        return user;
    }

    fun createVerificationToken(){

    }

    fun confirmAccount(token: String){

    }

   
}