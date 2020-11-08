package de.livepoll.api.controller

import de.livepoll.api.entity.db.User
import de.livepoll.api.entity.dto.UserDto
import de.livepoll.api.service.UserService
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {

    @Autowired
    private lateinit var userService: UserService

    @PostMapping("/register")
    fun createNewAccount(@RequestBody newUser: UserDto): String{
        userService.createAccount(userDtoToDao(newUser))
        return "Please confirm your email"
    }


    private fun userDtoToDao(userDto: UserDto): User {
        val mapper = ModelMapper()
        return mapper.map(userDto, User::class.java)
    }
}