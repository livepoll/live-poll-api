package de.livepoll.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.annotation.AuthenticationPrincipal
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger.web.UiConfiguration
import springfox.documentation.swagger.web.UiConfigurationBuilder

@Configuration
class SwaggerConfig {

    @Bean
    fun api(): Docket = Docket(DocumentationType.OAS_30)
            .ignoredParameterTypes(AuthenticationPrincipal::class.java)
            .host("api.live-poll.de")
            .enableUrlTemplating(true)
            .select()
            .apis(RequestHandlerSelectors.basePackage("de.livepoll.api"))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo())

    @Bean
    fun uiConfig(): UiConfiguration = UiConfigurationBuilder.builder()
            .defaultModelsExpandDepth(-1)
            .build()

    private fun apiInfo() = ApiInfo(
            "Live-Poll API",
            "Platform for providing surveys with live display features",
            "1.0.1",
            "https://chillibits.com/pmapp?p=privacy",
            Contact("ChilliBits", "https://www.chillibits.com", "contact@chillibits.com"),
            "ODC DbCL v1.0 License",
            "https://opendatacommons.org/licenses/dbcl/1.0/",
            emptyList()
    )
}