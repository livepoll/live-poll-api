package de.livepoll.api.cucumber

import de.livepoll.api.LivePollApplication
import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import io.cucumber.spring.CucumberContextConfiguration
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest

@RunWith(Cucumber::class)
@CucumberOptions(
        features = ["src/test/resources/features/"],
        plugin = ["pretty", "json:target/cucumber-report.json"])
@CucumberContextConfiguration
@SpringBootTest(classes = [LivePollApplication::class])
class CucumberTests {}