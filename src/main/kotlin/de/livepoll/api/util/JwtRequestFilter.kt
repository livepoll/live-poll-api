package de.livepoll.api.util

import de.livepoll.api.service.JwtUserDetailsService
import de.livepoll.api.util.jwtCookie.CookieCipher
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
class JwtRequestFilter(
        private val cookieCipher: CookieCipher
) : OncePerRequestFilter() {

    @Autowired
    private lateinit var jwtUserDetailsService: JwtUserDetailsService

    @Autowired
    private lateinit var jwtUtil: JwtUtil

    private val accessTokenCookieName = System.getenv("LIVE_POLL_JWT_AUTH_COOKIE_NAME")

    override fun doFilterInternal(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse, filterChain: FilterChain) {
        var token: String? = null
        var userName: String? = null

        if (httpServletRequest.cookies != null) {
            token = this.getJwtToken(httpServletRequest, true)
            if (token != null) {
                userName = jwtUtil.extractUsername(token)
            }
        } else if (httpServletRequest.getHeader("Authorization") != null) {
            token = this.getJwtToken(httpServletRequest, false)
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

    private fun getJwtFromRequest(request: HttpServletRequest): String? {
        val authorizationHeader = request.getHeader("Authorization")
        if (authorizationHeader.startsWith("Bearer ")) {
            val accessToken = authorizationHeader.substring(7)
            return cookieCipher.decrypt(accessToken)
        }
        return null
    }

    private fun getJwtFromCookie(request: HttpServletRequest): String? {
        request.cookies.forEach {
            if (accessTokenCookieName == it.name) {
                println("Token found")
                val accessToken = it.value ?: return null
                return cookieCipher.decrypt(accessToken)
            }
        }
        return null
    }

    private fun getJwtToken(request: HttpServletRequest, fromCookie: Boolean): String? {
        return if (fromCookie) getJwtFromCookie(request) else getJwtFromRequest(request)
    }
}
