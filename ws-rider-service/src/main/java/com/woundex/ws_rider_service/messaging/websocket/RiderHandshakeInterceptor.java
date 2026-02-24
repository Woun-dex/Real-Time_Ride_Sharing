package com.woundex.ws_rider_service.messaging.websocket;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.woundex.ws_rider_service.domain.value_object.RiderId;

/**
 * Extracts userId from query parameters during WebSocket handshake
 * and stores it as a RiderId session attribute for session registration.
 */
@Component
public class RiderHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RiderHandshakeInterceptor.class);

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
                if ("userId".equals(kv[0]) && kv.length == 2 && !kv[1].isBlank()) {
                    try {
                        RiderId riderId = RiderId.of(kv[1]);
                        attributes.put("riderId", riderId);
                        log.debug("Handshake: extracted riderId={} from query", riderId);
                    } catch (IllegalArgumentException e) {
                        log.warn("Handshake: invalid userId param: {}", kv[1]);
                    }
                    break;
                }
            }
        }

        // Also extract tripId from path for /ws/track/{tripId}
        String path = request.getURI().getPath();
        if (path.startsWith("/ws/track/")) {
            String tripId = path.substring("/ws/track/".length());
            if (!tripId.isBlank() && !tripId.contains("/")) {
                attributes.put("tripId", tripId);
                log.debug("Handshake: extracted tripId={} from path", tripId);
            }
        }

        return true; // allow handshake
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
