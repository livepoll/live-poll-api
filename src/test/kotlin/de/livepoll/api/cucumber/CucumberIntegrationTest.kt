package de.livepoll.api.cucumber

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith




@RunWith(Cucumber::class)
@CucumberOptions(
        strict=true,
        glue = ["features"],
        stepNotifications = true,
        features = ["src/test/resources/features"],
        plugin = ["pretty"])
class CucumberIntegrationTest: SpringIntegrationTest(){

}

