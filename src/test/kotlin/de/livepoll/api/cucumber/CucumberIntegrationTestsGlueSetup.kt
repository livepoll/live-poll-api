package de.livepoll.api.cucumber

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import io.cucumber.spring.CucumberContextConfiguration
import org.junit.runner.RunWith

@RunWith(Cucumber::class)
@CucumberOptions(
        features = ["src/test/resources/features/"],
        plugin = ["pretty", "html:target/cucumber-report.html"])
class CucumberIntegrationTestsGlueSetup {

}