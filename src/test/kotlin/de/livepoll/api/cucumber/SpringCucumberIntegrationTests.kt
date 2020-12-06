package de.livepoll.api.cucumber

import de.livepoll.api.LivePollApplication
import de.livepoll.api.entity.db.User
import de.livepoll.api.entity.jwt.AuthenticationRequest
import de.livepoll.api.repository.UserRepository
import io.cucumber.spring.CucumberContextConfiguration
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import org.assertj.core.api.Assertions.assertThat

// https://github.com/Mhverma/spring-cucumber-example/blob/master/src/test/java/com/manoj/training/app/SpringCucumberIntegrationTests.java
@RunWith(SpringRunner::class)
@CucumberContextConfiguration
@SpringBootTest(classes = [LivePollApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpringCucumberIntegrationTests(userRepository: UserRepository) {
    companion object {
        private const val AUTHENTICATION_ENDPOINT = "/v0/authenticate/login"
    }

    protected final var testUser: User = userRepository.findByUsername(System.getenv("TEST_USER_NAME"))!!

    // can't move this into companion object since
    // "Kotlin: Using non-JVM static members protected in the superclass companion is unsupported yet"
    protected val SERVER_URL = "https://localhost"

    @LocalServerPort
    protected var port = 0

    protected var restTemplate: RestTemplate = RestTemplate()

    protected fun logInWithTestUser(): Pair<HttpStatus, String> {
        // https://springbootdev.com/2017/11/21/spring-resttemplate-exchange-method/
        // https://attacomsian.com/blog/spring-boot-resttemplate-post-request-json-headers
        val url = "${SERVER_URL}:$port$AUTHENTICATION_ENDPOINT"

        // request body params
        val authenticationRequest = AuthenticationRequest(testUser.username, System.getenv("TEST_USER_PASSWORD"))
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

}
