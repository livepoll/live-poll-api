package de.livepoll.api.util

import de.livepoll.api.service.JwtUserDetailsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JwtRequestFilter: OncePerRequestFilter() {

    @Autowired
    private lateinit var jwtUserDetailsService: JwtUserDetailsService

    @Autowired
    private lateinit var jwtUtil: JwtUtil

    override fun doFilterInternal(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse, filterChain: FilterChain) {
        val authorizationHeader = httpServletRequest.getHeader("Authorization")
        var token: String? = null
        var userName: String? = null

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7)
            userName = jwtUtil.extractUsername(token)
        }

        if (userName != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails: UserDetails = jwtUserDetailsService.loadUserByUsername(userName)
            if (jwtUtil.validateToken(token, userDetails)) {
                SecurityContextHolder.getContext().authentication =
                        UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities).apply {
                    details = WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse)
    }
}
