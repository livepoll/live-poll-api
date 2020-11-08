package de.livepoll.api.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController {

    @Value("\${app.version}")
    private val appVersion: String = "undefined"

    @GetMapping("/version")
    fun getStatus(): Map<String, String> {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["api-version"] = appVersion
        return hashMap
    }

}
