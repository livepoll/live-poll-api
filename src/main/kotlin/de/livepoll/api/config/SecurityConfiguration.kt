package de.livepoll.api.config

import de.livepoll.api.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder


@Configuration
@EnableWebSecurity
class SecurityConfiguration: WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var userDetailsService: UserDetailsService

    override fun configure(http: HttpSecurity) {
        http

                .authorizeRequests()
                .antMatchers("/helloworld").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().and()
                .httpBasic();
    }

    @Autowired
    @Throws(Exception::class)
    fun globalSecurityConfiguration(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
    }

    @Bean
    fun getPasswordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}