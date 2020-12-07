package de.livepoll.api.cucumber.stepdefinitions

import de.livepoll.api.cucumber.CucumberIntegrationTestContext
import de.livepoll.api.repository.UserRepository
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClientResponseException

class UserStepDefinitions(userRepository: UserRepository) : CucumberIntegrationTestContext(userRepository) {
    private val USER_ENDPOINT = "/v0/users/${testUser.id}"
    private val USER_ENDPOINT_ANOTHER = "/v0/users/${testUser.id + 1}"

    lateinit var status: HttpStatus
    var alreadyConfirmed = false

    @Given("A test user exists")
    fun makeSureATestUserExists() {
        // TODO
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
        val url = "${SERVER_URL}:$port$USER_ENDPOINT_ANOTHER"
        try {
            val userResponseEntity = makeGetRequestWithSessionCookie<Any>(url, SessionCookieUtil.sessionCookie)
            assertThat(userResponseEntity.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        } catch (e: RestClientResponseException) {
            assertThat(e.rawStatusCode).isEqualTo(HttpStatus.UNAUTHORIZED.value())
        }
    }

}
