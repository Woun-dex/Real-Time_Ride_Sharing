package com.woundex.ws_driver_service.messaging.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woundex.ws_driver_service.domain.value_object.DriverId;
import com.woundex.ws_driver_service.dto.DriverPushMessage;

/**
 * Registry for managing active driver WebSocket sessions.
 */
@Component
public class SessionRegistry {

    private static final Logger log = LoggerFactory.getLogger(SessionRegistry.class);

    private final Map<DriverId, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper mapper;

    public SessionRegistry(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void register(WebSocketSession session) {
        Object raw = session.getAttributes().get("driverId");
        if (raw instanceof DriverId) {
            sessions.put((DriverId) raw, session);
            log.debug("Registered session {} for driver {}", session.getId(), raw);
        } else {
            log.warn("Unable to register session {}: missing or invalid driverId attribute", session.getId());
        }
    }

    public void unregister(WebSocketSession session) {
        sessions.entrySet().removeIf(e -> {
            boolean match = e.getValue() != null && e.getValue().getId().equals(session.getId());
            if (match) {
                log.debug("Unregistered session {} for driver {}", session.getId(), e.getKey());
            }
            return match;
        });
    }

    public void push(DriverId driverId, DriverPushMessage msg) {
        WebSocketSession session = sessions.get(driverId);
        if (session != null && session.isOpen()) {
            try {
                String json = mapper.writeValueAsString(msg);
                session.sendMessage(new TextMessage(json));
                log.info("\uD83D\uDCE4 Pushed {} to driver {} (session {})", msg.getType(), driverId, session.getId());
            } catch (IOException e) {
                log.error("Failed to push message to driver {}: {}", driverId, e.getMessage());
            }
        } else {
            log.warn("\u274C No open session for driver {} (registered sessions: {})", driverId, sessions.keySet());
        }
    }

    public boolean isConnected(DriverId driverId) {
        WebSocketSession session = sessions.get(driverId);
        return session != null && session.isOpen();
    }

    /**
     * Push a raw JSON string to the driver's session.
     */
    public void pushRaw(DriverId driverId, String jsonMessage) {
        WebSocketSession session = sessions.get(driverId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(jsonMessage));
                log.info("📤 Pushed raw message to driver {} (session {})", driverId, session.getId());
            } catch (IOException e) {
                log.error("Failed to push raw message to driver {}: {}", driverId, e.getMessage());
            }
        } else {
            log.warn("❌ No open session for driver {} (registered sessions: {})", driverId, sessions.keySet());
        }
    }
}
