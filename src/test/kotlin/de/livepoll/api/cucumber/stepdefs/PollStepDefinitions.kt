package de.livepoll.api.cucumber.stepdefs

import de.livepoll.api.cucumber.SpringCucumberIntegrationTests
import de.livepoll.api.entity.db.Poll
import de.livepoll.api.entity.dto.PollDtoIn
import de.livepoll.api.entity.dto.PollDtoOut
import de.livepoll.api.repository.PollRepository
import de.livepoll.api.repository.UserRepository
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.web.client.exchange
import java.sql.Date

class PollStepDefinitions(userRepository: UserRepository) : SpringCucumberIntegrationTests(userRepository) {
    @Autowired
    private lateinit var pollRepository: PollRepository

    private val POLLS_ENDPOINT = "/v0/users/${testUser.id}/polls"
    private val CREATE_POLL_ENDPOINT = "/v0/users/${testUser.id}/poll"
    var sessionCookie: String = ""
    var myPolls: ArrayList<PollDtoOut> = ArrayList()

    @Given("I am logged in as test user")
    fun beLoggedInAsSpecificUser() {
        val (status, sessionCookie) = logInWithTestUser()
        this.sessionCookie = sessionCookie
        assertThat(status).isEqualTo(HttpStatus.OK)
    }

    @Given("I have no polls created yet")
    fun deleteAllPolls() {
        pollRepository.deleteByUser(testUser)
    }

    @When("I create {int} new dummy polls")
    fun createNewPolls(numberOfPolls: Int) {
        val url = "${SERVER_URL}:$port$CREATE_POLL_ENDPOINT"
        for (i in 1..numberOfPolls) {
            // request body params & headers
            val pollPostRequest = PollDtoIn("DummyPoll_$i", Date(0), Date(0))
            val headers = HttpHeaders()
            headers["Cookie"] = sessionCookie
            val requestEntity: HttpEntity<PollDtoIn> = HttpEntity(pollPostRequest, headers)

            // make request
            val responseEntity: ResponseEntity<Any> = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity
            )

            assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.CREATED)
        }
    }

    @And("I retrieve my polls")
    fun retrieveMyPolls() {
        val url = "${SERVER_URL}:$port$POLLS_ENDPOINT"
        // request body params & headers
        val headers = HttpHeaders()
        headers["Cookie"] = sessionCookie
        val requestEntity: HttpEntity<String> = HttpEntity("", headers)

        // make request
        // val pollResponseEntity: ResponseEntity<Array<Poll>> = restTemplate.getForEntity(url)
        val pollResponseEntity: ResponseEntity<Array<PollDtoOut>> = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity
        )
        assertThat(pollResponseEntity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(pollResponseEntity.body).isNotNull
        for (poll in pollResponseEntity.body!!) {
            myPolls.add(poll)
        }
    }

    @Then("I get back exactly {int} polls")
    fun getBackPolls(numberOfPolls: Int) {
        assertThat(myPolls.size).isEqualTo(numberOfPolls)
    }

}
