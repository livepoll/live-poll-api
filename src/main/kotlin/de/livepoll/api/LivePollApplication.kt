package de.livepoll.api

import de.livepoll.api.account.AccountService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["de.livepoll.api"])
class LivePollApplication(
	private val accountService: AccountService,
): CommandLineRunner {
	override fun run(vararg args: String?) {
		// Create postman user, when started in testing environment
		if (System.getenv("LIVE_POLL_POSTMAN") == "true") accountService.createPostmanAccount()
	}
}

fun main(args: Array<String>) {
	runApplication<LivePollApplication>(*args)
}