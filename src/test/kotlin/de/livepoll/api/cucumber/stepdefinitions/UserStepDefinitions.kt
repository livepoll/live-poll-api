package de.livepoll.api.cucumber.stepdefinitions

import de.livepoll.api.cucumber.CucumberIntegrationTest
import de.livepoll.api.poll.Poll
import de.livepoll.api.entity.db.PollItem
import de.livepoll.api.user.User
import de.livepoll.api.poll.PollRepository
import de.livepoll.api.user.UserRepository
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.*
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.exchange
import java.util.*

private const val LOGOUT_ENDPOINT = "/v1/account/logout"

class UserStepDefinitions(
    private val pollRepository: PollRepository,
    private val userRepository: UserRepository
) : CucumberIntegrationTest(userRepository) {

    private val USER_ENDPOINT = "/v1/user"
    private val POLL_ENDPOINT = "/v1/polls"

    lateinit var status: HttpStatus
    var alreadyConfirmed = false

    @Given("A test user exists")
    fun makeSureATestUserExists() {
        assertThat(testUser)
    }

    @Given("I am logged in as test user")
    @When("I log in as test user")
    fun logInAsTestUser() {
        val (status, sessionCookie) = logInWithTestUser()
        SessionCookieUtil.sessionCookie = sessionCookie
        this.status = status
        confirmTestUserLogin()
    }

    @Then("My log in is confirmed")
    fun confirmTestUserLogin() {
        if (!alreadyConfirmed) {
            assertThat(status).isEqualTo(HttpStatus.OK)
            alreadyConfirmed = true
        }
    }

    @And("I am authorized to retrieve information about my own user")
    fun getInfoAboutTestUser() {
        val url = "${SERVER_URL}:$port$USER_ENDPOINT"
        val userResponseEntity = makeGetRequestWithSessionCookie<Any>(url, SessionCookieUtil.sessionCookie)
        assertThat(userResponseEntity.statusCode).isEqualTo(HttpStatus.OK)
    }

    @And("I am not authorized to retrieve information about a different user")
    fun getInfoAboutDifferentUser() {
        if(userRepository.findByUsername("different_user") == null){
         userRepository.saveAndFlush(User(1,"different_user", "different_email", passwordEncoder.encode("12345"), true, "ROLE_USER", emptyList()))
        }
        if(pollRepository.findBySlug("different_user_test_slug") == null){
            pollRepository.saveAndFlush(Poll(0, userRepository.findByUsername("different_user")!!, "different_user_test_poll", GregorianCalendar(2021, 5, 2).time, GregorianCalendar(2021, 5, 2).time, "different_user_test_slug", null, emptyList<PollItem>().toMutableList()))
        }
        val id = pollRepository.findBySlug("different_user_test_slug")!!.id
        val url = "${SERVER_URL}:$port$POLL_ENDPOINT/${id}"
        try {
            val pollResponseEntity = makeGetRequestWithSessionCookie<Any>(url, SessionCookieUtil.sessionCookie)
            assertThat(pollResponseEntity.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
        } catch (e: RestClientResponseException) {
            assertThat(e.rawStatusCode).isEqualTo(HttpStatus.FORBIDDEN.value())
        }
    }

    @When("I log myself out")
    fun logTestUserOut() {
        val url = "${SERVER_URL}:$port$LOGOUT_ENDPOINT"
        // request body params & headers
        val headers = HttpHeaders()
        headers["Cookie"] = SessionCookieUtil.sessionCookie
        val logOutRequestEntity: HttpEntity<String> = HttpEntity("", headers)

        // make request
        val logOutResponseEntity: ResponseEntity<Any> = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                logOutRequestEntity
        )
        assertThat(logOutResponseEntity.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Then("I can't access any data anymore")
    fun tryToGetInfoAboutSomeUsers() {
        val url = "${SERVER_URL}:$port$USER_ENDPOINT"
        try {
            val userResponseEntity = makeGetRequestWithSessionCookie<Any>(url, SessionCookieUtil.sessionCookie)
            assertThat(userResponseEntity.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
        } catch (e: RestClientResponseException) {
            assertThat(e.rawStatusCode).isEqualTo(HttpStatus.FORBIDDEN.value())
        }

        getInfoAboutDifferentUser()
    }

}
