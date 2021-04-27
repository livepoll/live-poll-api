package de.livepoll.api.config

import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor


class HttpHandshakeInterceptor: HandshakeInterceptor {
    override fun beforeHandshake(request: ServerHttpRequest, response: ServerHttpResponse, webSocketHandler: WebSocketHandler, attributes: MutableMap<String, Any>): Boolean {
        if (request is ServletServerHttpRequest) {
            val servletRequest = request as ServletServerHttpRequest
            val session = servletRequest.servletRequest.session
            attributes["sessionId"] = session.id
        }
        println("test. ID: "+attributes.getValue("sessionId"))
        return true
    }

    override fun afterHandshake(p0: ServerHttpRequest, p1: ServerHttpResponse, p2: WebSocketHandler, p3: Exception?) {

    }
}