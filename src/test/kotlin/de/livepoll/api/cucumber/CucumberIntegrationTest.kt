package de.livepoll.api.cucumber

import de.livepoll.api.LivePollApplication
import de.livepoll.api.user.User
import de.livepoll.api.authentication.AuthenticationRequest
import de.livepoll.api.user.UserRepository
import io.cucumber.spring.CucumberContextConfiguration
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext


// https://github.com/Mhverma/spring-cucumber-example/blob/master/src/test/java/com/manoj/training/app/SpringCucumberIntegrationTests.java
@CucumberContextConfiguration
@SpringBootTest(classes = [LivePollApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CucumberIntegrationTest(
        private val userRepository: UserRepository
) {

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @LocalServerPort
    protected var port = 0

    companion object {
        private const val AUTHENTICATION_ENDPOINT = "/v1/account/login"
    }

    object SessionCookieUtil {
        lateinit var sessionCookie: String
    }

    protected var testUser: User? = userRepository.findByUsername("cucumber_test_user")

    // can't move this into companion object since
    // "Kotlin: Using non-JVM static members protected in the superclass companion is unsupported yet"
    protected val SERVER_URL = "https://localhost"
    protected final var restTemplate: RestTemplate

    protected final val testUserName = "cucumber_test_user"
    protected final val testUserPassword = "1234"

    init {
        // https://stackoverflow.com/a/42689331/9655481
        // Disable SSL certificate checking with Spring RestTemplate
        // ⚠ only do this for Cucumber testing purposes against https://localhost
        val acceptingTrustStrategy = { chain: Array<X509Certificate?>?, authType: String? -> true }

        val sslContext: SSLContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build()

        val csf = SSLConnectionSocketFactory(sslContext)

        val httpClient: CloseableHttpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build()

        val requestFactory = HttpComponentsClientHttpRequestFactory()

        requestFactory.httpClient = httpClient
        restTemplate = RestTemplate(requestFactory)
    }

    protected fun logInWithTestUser(): Pair<HttpStatus, String> {
        if (userRepository.findByUsername(testUserName) == null) {
            userRepository.saveAndFlush(User(0, testUserName, "email", passwordEncoder.encode(testUserPassword), true, "ROLE_USER", emptyList()))
            testUser = userRepository.findByUsername(testUserName)
        }
        // https://springbootdev.com/2017/11/21/spring-resttemplate-exchange-method/
        // https://attacomsian.com/blog/spring-boot-resttemplate-post-request-json-headers
        val url = "${SERVER_URL}:$port$AUTHENTICATION_ENDPOINT"

        // request body params
        val authenticationRequest = AuthenticationRequest(testUserName, testUserPassword)
        val requestEntity: HttpEntity<AuthenticationRequest> = HttpEntity(authenticationRequest)

        // make request
        val responseEntity: ResponseEntity<Any> = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity
        )

        // store session cookie (which includes the JWT token)
        val setCookie: String? = responseEntity.headers.getFirst(HttpHeaders.SET_COOKIE)
        assertThat(setCookie).isNotNull
        return Pair(responseEntity.statusCode, setCookie!!)
    }

    protected final inline fun <reified T> makeGetRequestWithSessionCookie(url: String, sessionCookie: String): ResponseEntity<T> {
        // request body params & headers
        val headers = HttpHeaders()
        headers["Cookie"] = sessionCookie
        val requestEntity: HttpEntity<String> = HttpEntity("", headers)

        // make request
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity
        )
    }

}
