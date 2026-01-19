package com.woundex.ws_driver_service.messaging.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woundex.ws_driver_service.application.handler.DriverAvailabilityHandler;
import com.woundex.ws_driver_service.application.handler.DriverGpsMessageHandler;
import com.woundex.ws_driver_service.dto.DriverAvailabilityMessage;
import com.woundex.ws_driver_service.dto.DriverGpsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * WebSocket handler for driver connections.
 * Handles GPS updates and availability state changes.
 */
@Component
public class DriverWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(DriverWebSocketHandler.class);

    private final SessionRegistry sessions;
    private final DriverGpsMessageHandler gpsHandler;
    private final DriverAvailabilityHandler availabilityHandler;
    private final ObjectMapper mapper;
    private final Executor executor;

    public DriverWebSocketHandler(
            SessionRegistry sessions,
            DriverGpsMessageHandler gpsHandler,
            DriverAvailabilityHandler availabilityHandler,
            ObjectMapper mapper,
            Executor executor
    ) {
        this.sessions = Objects.requireNonNull(sessions, "sessions must not be null");
        this.gpsHandler = Objects.requireNonNull(gpsHandler, "gpsHandler must not be null");
        this.availabilityHandler = Objects.requireNonNull(availabilityHandler, "availabilityHandler must not be null");
        this.mapper = Objects.requireNonNull(mapper, "mapper must not be null");
        this.executor = Objects.requireNonNull(executor, "executor must not be null");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Driver connected: {}", session.getId());
        sessions.register(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Driver disconnected: {}", session.getId());
        sessions.unregister(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        final String payload = message.getPayload();

        executor.execute(() -> {
            try {
                if (payload.contains("\"heading\"") || payload.contains("\"speed\"")) {
                    DriverGpsMessage gps = mapper.readValue(payload, DriverGpsMessage.class);
                    gpsHandler.handle(gps);
                    sendAck(session);
                } else if (payload.contains("\"availability\"")) {
                    DriverAvailabilityMessage availability = mapper.readValue(payload, DriverAvailabilityMessage.class);
                    availabilityHandler.handle(availability);
                    sendAck(session);
                } else {
                    log.warn("Unknown message type from session {}: {}", session.getId(), payload);
                    sendError(session, "unknown_message_type");
                }
            } catch (Exception e) {
                log.error("Error handling message for session {}: {}", session.getId(), e.getMessage(), e);
                sendError(session, "internal_error");
            }
        });
    }

    private void sendAck(WebSocketSession session) {
        try {
            session.sendMessage(new TextMessage("{\"status\":\"ok\"}"));
        } catch (Exception ignored) {
        }
    }

    private void sendError(WebSocketSession session, String code) {
        try {
            session.sendMessage(new TextMessage("{\"status\":\"error\",\"code\":\"" + code + "\"}"));
        } catch (Exception ignored) {
        }
    }
}
