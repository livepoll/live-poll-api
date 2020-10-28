package de.livepoll.api.cucumber

import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import junit.framework.Assert.assertEquals

class StepDefinitionIntegrationTest : SpringIntegrationTest(){

    var version=0.0

    @When("^the client calls /version$")
    @Throws(Throwable::class)
    fun the_client_issues_GET_version() {
        version = 1.0

    }

    @Then("^the client receives status code of (\\d+)$")
    @Throws(Throwable::class)
    fun the_client_receives_status_code_of(statusCode: Int) {

    }

    @And("^the client receives server version (.+)$")
    @Throws(Throwable::class)
    fun the_client_receives_server_version_body(version: String?) {
        assertEquals(this.version, version)
    }
    @When("^a")
    fun a(){
        version = 1.0
    }

}
