package de.livepoll.api.cucumber

import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith
import org.springframework.http.HttpStatus




@RunWith(Cucumber::class)
@CucumberOptions(features = ["src/test/kotlin/de.livepoll.api/featurefiles"])
class CucumberIntegrationTest: SpringIntegrationTest(){

}

