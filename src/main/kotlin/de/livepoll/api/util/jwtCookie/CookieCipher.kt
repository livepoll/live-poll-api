package de.livepoll.api.util.jwtCookie

import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

@Component
class CookieCipher {

    private val KEY_VALUE = System.getenv("LIVE_POLL_JWT_COOKIE_KEY_VALUE")
    private var secretKey: SecretKeySpec? = null
    private var key: ByteArray? = null

    fun setKey() {
        try {
            key = MessageDigest.getInstance("SHA-1").digest(KEY_VALUE.toByteArray(StandardCharsets.UTF_8))
            key = Arrays.copyOf(key, 16)
            secretKey = SecretKeySpec(key, "AES")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    fun encrypt(stringToEncrypt: String): String {
        try {
            setKey()
            val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return Base64.getEncoder().encodeToString(cipher.doFinal(stringToEncrypt.toByteArray(StandardCharsets.UTF_8)))
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error in cookieCipher")
        }
    }


    fun decrypt(strToDecrypt: String): String {
        try {
            setKey()
            val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            return String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)))
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error in cookieCipher")
        }
    }
}