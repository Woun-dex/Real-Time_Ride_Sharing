package com.woundex.ws_driver_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.woundex.ws_driver_service.messaging.websocket.DriverHandshakeInterceptor;
import com.woundex.ws_driver_service.messaging.websocket.DriverWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final DriverWebSocketHandler handler;
    private final DriverHandshakeInterceptor handshakeInterceptor;

    public WebSocketConfig(DriverWebSocketHandler handler, DriverHandshakeInterceptor handshakeInterceptor) {
        this.handler = handler;
        this.handshakeInterceptor = handshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/driver", "/ws/driver/notifications")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("*");
    }
}
