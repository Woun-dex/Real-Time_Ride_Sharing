package com.woundex.ws_rider_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.woundex.ws_rider_service.messaging.websocket.RiderHandshakeInterceptor;
import com.woundex.ws_rider_service.messaging.websocket.RiderWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final RiderWebSocketHandler handler;
    private final RiderHandshakeInterceptor handshakeInterceptor;

    public WebSocketConfig(RiderWebSocketHandler handler, RiderHandshakeInterceptor handshakeInterceptor) {
        this.handler = handler;
        this.handshakeInterceptor = handshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/rider", "/ws/rider/notifications", "/ws/track/**")
            .addInterceptors(handshakeInterceptor)
            .setAllowedOrigins("*");
    }
}