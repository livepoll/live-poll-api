package de.livepoll.api.websocket

import org.springframework.http.server.ServerHttpRequest
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.support.DefaultHandshakeHandler
import java.security.Principal
import java.util.*

private const val ATTR_PRINCIPAL = "_principal_"

class CustomWebSocketHandshakeHandler : DefaultHandshakeHandler() {

    override fun determineUser(request: ServerHttpRequest, wsHandler: WebSocketHandler, attributes: MutableMap<String, Any>): Principal {
        val name: String
        if (!attributes.containsKey(ATTR_PRINCIPAL)) {
            name = UUID.randomUUID().toString()
            attributes[ATTR_PRINCIPAL] = name
        } else {
            name = attributes[ATTR_PRINCIPAL] as String
        }
        return Principal { name }
    }

}