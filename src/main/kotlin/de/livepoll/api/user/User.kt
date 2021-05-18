package de.livepoll.api.user

import com.fasterxml.jackson.annotation.JsonIgnore
import de.livepoll.api.poll.Poll
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Entity
@Table(name = "user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    var id: Long,

    @Column(unique = true, nullable = false)
    private val username: String,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = false)
    private var password: String,

    @Column(name = "account_status", nullable = false)
    var isAccountEnabled: Boolean,

    @Column(nullable = false)
    var roles: String,

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    val polls: List<Poll>
) : UserDetails {

    override fun getUsername(): String {
        return username
    }

    override fun getPassword(): String {
        return password
    }

    fun setPassword(password: String) {
        this.password = password
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        // add user's authorities
        val authorities = mutableListOf<GrantedAuthority>()
        getRoles().forEach { authorities.add(SimpleGrantedAuthority(it)) }
        return authorities
    }

    private fun getRoles(): List<String> {
        return roles.split(",")
    }

    override fun isEnabled(): Boolean {
        return isAccountEnabled
    }

    override fun isAccountNonExpired(): Boolean {
        return isAccountEnabled
    }

    override fun isAccountNonLocked(): Boolean {
        return isAccountEnabled
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true // TODO
    }

}
