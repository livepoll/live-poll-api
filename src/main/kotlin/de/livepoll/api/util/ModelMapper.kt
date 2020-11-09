package de.livepoll.api.util

import de.livepoll.api.entity.db.User
import de.livepoll.api.entity.dto.UserDto
import org.modelmapper.ModelMapper

private val mapper = ModelMapper()

fun User.toDto() = mapper.map(this, UserDto::class.java)
fun UserDto.toDao() = mapper.map(this, User::class.java)
