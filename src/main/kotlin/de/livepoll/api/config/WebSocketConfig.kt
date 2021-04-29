package de.livepoll.api.config

import de.livepoll.api.util.websocket.CustomWebSocketHandshakeHandler
import de.livepoll.api.util.websocket.HttpHandshakeInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer


@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
        private val corsConfig: CorsConfig
) : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/v1/websocket/poll")
        registry.setApplicationDestinationPrefixes("/v/1/websocket/answer")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/v1/websocket/enter-poll")
                .addInterceptors(HttpHandshakeInterceptor())
                .setHandshakeHandler(CustomWebSocketHandshakeHandler())
                .setAllowedOrigins("*")
    }

}