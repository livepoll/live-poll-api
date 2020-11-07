package de.livepoll.api.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
class CorsConfig: WebMvcConfigurer {

    private val allowedOrigins = setOf(
            "http://192.168.0.52:4200",
            "https://dev.live-poll.de",
            "https://live-poll.de"
    )

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "HEAD", "POST", "PUT")
                .allowedOrigins(*allowedOrigins.toTypedArray())
    }
}