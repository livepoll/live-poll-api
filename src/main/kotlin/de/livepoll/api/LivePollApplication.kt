package de.livepoll.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["de.live.poll.api"])
class LivePollApplication

fun main(args: Array<String>) {
	runApplication<LivePollApplication>(*args)
}