package de.livepoll.api.util.jwtCookie

import de.livepoll.api.util.TOKEN_DURATION
import org.springframework.http.HttpCookie
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component

@Component
class CookieUtil(
        private val cookieCipher: CookieCipher
) {

    private val accessTokenCookieName = System.getenv("LIVE_POLL_JWT_AUTH_COOKIE_NAME")
    private val isTLSEncrypted = System.getenv("LIVE_POLL_SERVER_URL").startsWith("https://")
    private val isDevServer = System.getenv("LIVE_POLL_SERVER_URL").contains("localhost")
    private val domain = if(isDevServer) "localhost" else "live-poll.de"

    fun createAccessTokenCookie(token: String): HttpCookie? {

        val encryptedToken = cookieCipher.encrypt(token)
        return ResponseCookie.from(accessTokenCookieName, encryptedToken)
                .maxAge(TOKEN_DURATION)
                .httpOnly(true)
                // Secure is only supported with https
                .secure(isTLSEncrypted)
                // Use top level domain. Subdomains are included automatically (https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Set-Cookie)
                .domain(domain)
                .path("/")
                .build()
    }


    fun deleteAccessTokenCookie(): HttpCookie? {
        return ResponseCookie.from(accessTokenCookieName, "")
                .maxAge(0)
                .httpOnly(true)
                // Secure is only supported with https
                .secure(isTLSEncrypted)
                // Use top level domain. Subdomains are included automatically (https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Set-Cookie)
                .domain(domain)
                .path("/")
                .build()
    }
}