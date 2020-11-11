package de.livepoll.api.util.jwtCookie

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpCookie
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component


@Component
class CookieUtil(
        private val cookieCipher: CookieCipher
) {

    @Value("\${JWT_AUTHENTICATION_COOKIE_NAME}")
    private val accessTokenCookieName: String? = null

    fun createAccessTokenCookie(token: String, duration: Long): HttpCookie? {
        val encryptedToken: String = cookieCipher.encrypt(token)
        return ResponseCookie.from(accessTokenCookieName!!, encryptedToken)
                .maxAge(duration)
                .httpOnly(true)
                .path("/")
                .build()
    }


    fun deleteAccessTokenCookie(): HttpCookie? {
        return ResponseCookie.from(accessTokenCookieName!!, "").maxAge(0).httpOnly(true).path("/").build()
    }
}