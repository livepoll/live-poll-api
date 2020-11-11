package de.livepoll.api.util.jwtCookie

import org.springframework.stereotype.Component
import java.security.NoSuchAlgorithmException
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Cipher
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.Arrays
import java.util.Base64

@Component
class CookieCipher {

    private val KEYVALUE = "secureKey"
    private var secretKey: SecretKeySpec? = null
    private var key: ByteArray? = null

    fun setKey() {
        try {
            key = MessageDigest.getInstance("SHA-1").digest(KEYVALUE.toByteArray(StandardCharsets.UTF_8))
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