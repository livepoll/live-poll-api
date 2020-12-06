package de.livepoll.api.cucumber

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import io.cucumber.spring.CucumberContextConfiguration
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
        features = ["src/test/resources/features/poll.feature"],
        plugin = ["pretty", "html:target/cucumber"])
class PollCucumberIntegrationTest {

}