package de.livepoll.api.util.jwtCookie

import de.livepoll.api.util.TOKEN_DURATION
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component

@Component
class CookieUtil(
    private val cookieCipher: CookieCipher
) {

    private val accessTokenCookieName = System.getenv("LIVE_POLL_JWT_AUTH_COOKIE_NAME")
    private val isTLSEncrypted = System.getenv("LIVE_POLL_SERVER_URL").startsWith("https://")
    private val isDevServer = System.getenv("LIVE_POLL_SERVER_URL").contains("localhost")
    private val domain = if (isDevServer) "localhost" else "live-poll.de"

    fun createAccessTokenCookie(token: String) = buildCookie(cookieCipher.encrypt(token))
    fun deleteAccessTokenCookie() = buildCookie("")

    fun buildCookie(content: String) = ResponseCookie.from(accessTokenCookieName, content)
        .maxAge(if (content.isBlank()) 0 else TOKEN_DURATION)
        .httpOnly(true)
        // Secure is only supported with https
        .secure(isTLSEncrypted)
        // Use top level domain. Subdomains are included automatically (https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Set-Cookie)
        .domain(domain)
        .sameSite("None")
        .path("/")
        .build()
}
