package com.woundex.ws_driver_service.messaging.websocket;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.woundex.ws_driver_service.domain.value_object.DriverId;

/**
 * Extracts driverId from the WebSocket handshake query parameter
 * and stores it in session attributes so SessionRegistry can map sessions.
 *
 * Expected connection URL: /ws/driver/notifications?driverId=<uuid>
 */
@Component
public class DriverHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(DriverHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        String query = request.getURI().getQuery();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] kv = param.split("=", 2);
                if ("driverId".equals(kv[0]) && kv.length == 2 && !kv[1].isBlank()) {
                    try {
                        DriverId driverId = DriverId.of(UUID.fromString(kv[1]));
                        attributes.put("driverId", driverId);
                        log.info("Handshake: extracted driverId={} from query", driverId);
                        return true;
                    } catch (IllegalArgumentException e) {
                        log.warn("Handshake: invalid driverId param: {}", kv[1]);
                    }
                }
            }
        }

        log.warn("Handshake: no driverId query parameter found in URI: {}", request.getURI());
        return true; // still allow connection — session just won't receive targeted pushes
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // no-op
    }
}
