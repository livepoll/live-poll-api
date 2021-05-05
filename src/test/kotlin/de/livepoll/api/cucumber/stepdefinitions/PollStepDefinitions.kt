package de.livepoll.api.cucumber.stepdefinitions

import de.livepoll.api.cucumber.CucumberIntegrationTest
import de.livepoll.api.entity.dto.PollDtoIn
import de.livepoll.api.entity.dto.PollDtoOut
import de.livepoll.api.repository.PollRepository
import de.livepoll.api.repository.UserRepository
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import org.assertj.core.api.Assertions.assertThat
import org.springframework.http.*
import org.springframework.web.client.exchange
import java.sql.Date

class PollStepDefinitions(
        userRepository: UserRepository,
        private val pollRepository: PollRepository
) : CucumberIntegrationTest(userRepository) {
    private val POLL_ENDPOINT = "/v1/polls"

    lateinit var myPolls: ArrayList<PollDtoOut>

    @Given("I have no polls created yet")
    fun deleteAllPolls() {
        pollRepository.deleteByUser(testUser!!)
    }

    @When("I create {int} new dummy polls")
    fun createNewPolls(numberOfPolls: Int) {
        for (i in 1..numberOfPolls) {
            createNewDummyPoll("DummyPoll_$i")
        }
    }

    private fun createNewDummyPoll(pollName: String) {
        // Make sure we start with a new ArrayList for myPolls
        myPolls = ArrayList()

        val url = "${SERVER_URL}:$port$POLL_ENDPOINT"

        // request body params & headers
        val pollPostRequest = PollDtoIn(pollName, Date(0), Date(0), "test", null)
        val headers = HttpHeaders()
        headers["Cookie"] = SessionCookieUtil.sessionCookie
        val requestEntity: HttpEntity<PollDtoIn> = HttpEntity(pollPostRequest, headers)

        // make request
        val responseEntity: ResponseEntity<Any> = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity
        )

        assertThat(responseEntity.statusCode).isEqualTo(HttpStatus.CREATED)
    }

    @When("I create a poll named {string}")
    fun createNamedPoll(pollName: String) {
        createNewDummyPoll(pollName)
    }

    @And("I retrieve my polls")
    fun retrieveMyPolls() {
        val url = "${SERVER_URL}:$port$POLL_ENDPOINT"
        val pollResponseEntity =
                makeGetRequestWithSessionCookie<Array<PollDtoOut>>(url, SessionCookieUtil.sessionCookie)
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

    @And("I get back a poll named {string}")
    fun getBackNamedPoll(pollName: String) {
        assertThat(myPolls[0].name).isEqualTo(pollName)
    }

}
