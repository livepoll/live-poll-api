package de.livepoll.api.cucumber

import de.livepoll.api.LivePollApplication
import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("INTEGRATION_TEST")
@CucumberContextConfiguration
@SpringBootTest(classes = [LivePollApplication::class], webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SpringIntegrationTest {
    //implement functions here to use them in stepDefinitionIntegrationTest
}