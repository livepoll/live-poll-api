package de.livepoll.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.view.InternalResourceViewResolver

@Configuration
@EnableWebMvc
class CorsConfig : WebMvcConfigurer {

    private val allowedOrigins = setOf(
            System.getenv("LIVE_POLL_DEV_URL"),
            "https://dev.live-poll.de",
            "https://live-poll.de"
    )

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
                .allowedMethods("GET", "HEAD", "POST", "PUT")
                .allowedOrigins(*allowedOrigins.toTypedArray())
                .allowCredentials(true)
    }

    @Bean
    fun defaultViewResolver() = InternalResourceViewResolver()
}