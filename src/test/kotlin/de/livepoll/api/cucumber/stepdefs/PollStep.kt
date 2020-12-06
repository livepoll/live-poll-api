package de.livepoll.api.cucumber.stepdefs

import io.cucumber.java.PendingException
import io.cucumber.java.en.Then
import io.cucumber.java.en.When

class PollStep {

    @When("the client calls \\/version")
    fun the_client_calls_version() {
        throw PendingException()
    }

    @Then("the client receives server version 1.0")
    fun test2() {
        throw PendingException()
    }

}
