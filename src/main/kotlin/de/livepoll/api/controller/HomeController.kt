package de.livepoll.api.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController {

    @Value("\${api.version}")
    private val apiVersion: String = "undefined"

    @GetMapping("/version")
    fun getStatus(): Map<String, String> {
        return mapOf("api-version" to apiVersion)
    }

}
