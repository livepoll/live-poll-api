package de.livepoll.api.util.jwtCookie

import org.springframework.http.HttpCookie
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component

@Component
class CookieUtil(
        private val cookieCipher: CookieCipher
) {

    private val accessTokenCookieName = System.getenv("LIVE_POLL_JWT_AUTH_COOKIE_NAME")
    private val isTLSEncrypted = System.getenv("LIVE_POLL_SERVER_URL").startsWith("https://")

    fun createAccessTokenCookie(token: String, duration: Long): HttpCookie? {
        val encryptedToken: String = cookieCipher.encrypt(token)
        return ResponseCookie.from(accessTokenCookieName, encryptedToken)
                .maxAge(duration)
                .httpOnly(true)
                .secure(isTLSEncrypted)
                .path("/")
                .build()
    }


    fun deleteAccessTokenCookie(): HttpCookie? {
        return ResponseCookie.from(accessTokenCookieName, "")
                .maxAge(0)
                .httpOnly(true)
                .secure(isTLSEncrypted)
                .path("/")
                .build()
    }
}