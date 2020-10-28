package de.livepoll.api.entity

import de.livepoll.api.entity.db.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails



class AuthenticatedUser(var user: User): User(user), UserDetails{

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        TODO("Not yet implemented")
    }


    override fun getPassword(): String {
        return user.getPassword()
    }

    override fun getUsername(): String {
        return user.getUsername()
    }

    override fun isAccountNonExpired(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isAccountNonLocked(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isCredentialsNonExpired(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isEnabled(): Boolean {
        TODO("Not yet implemented")
    }

}