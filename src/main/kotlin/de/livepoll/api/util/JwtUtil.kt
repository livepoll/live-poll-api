package de.livepoll.api.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Function

@Service
class JwtUtil {

    private val secret = System.getenv("LIVE_POLL_JWT_SECRET")

    fun extractUsername(token: String?): String = extractClaim(token) { obj: Claims -> obj.subject }

    fun extractExpiration(token: String?): Date = extractClaim(token) { obj: Claims -> obj.expiration }

    fun <T> extractClaim(token: String?, claimsResolver: Function<Claims, T>): T {
        val claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    private fun extractAllClaims(token: String?): Claims {
        return Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(token)
                .body
    }

    private fun isTokenExpired(token: String?) = extractExpiration(token).before(Date())

    fun generateToken(userDetails: UserDetails) = createToken(mutableMapOf(), userDetails.username)

    private fun createToken(claims: Map<String, Any>, subject: String): String {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject).setIssuedAt(Date(System.currentTimeMillis()))
                .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, secret).compact()
    }

    fun validateToken(token: String?, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username == userDetails.username && !isTokenExpired(token)
    }
}