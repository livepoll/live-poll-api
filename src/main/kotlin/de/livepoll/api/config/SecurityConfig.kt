package de.livepoll.api.config

import de.livepoll.api.service.JwtUserDetailsService
import de.livepoll.api.util.JwtRequestFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtUserDetailsService: JwtUserDetailsService,
    private val jwtRequestFilter: JwtRequestFilter
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.cors().and().csrf().disable()
            .authorizeRequests()
            .antMatchers("/v1/account/register").permitAll()
            .antMatchers("/v1/account/confirm").permitAll()
            .antMatchers("/v1/account/login").permitAll()
            .antMatchers("/v1/websocket/**").permitAll()
            .antMatchers("/actuator/**").permitAll()
            //.antMatchers("/admin").hasRole("ADMIN") // TODO: introduce ROLE_ADMIN authority later on
            .anyRequest().authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder())
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    // https://github.com/spring-projects/spring-boot/issues/11136
    @Bean
    override fun authenticationManagerBean(): AuthenticationManager = super.authenticationManagerBean()

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers(
            "/v3/api-docs",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/"
        )
    }
}
