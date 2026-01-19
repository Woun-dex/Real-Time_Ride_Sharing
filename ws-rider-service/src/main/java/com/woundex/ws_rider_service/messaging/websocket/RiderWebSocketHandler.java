
package com.woundex.ws_rider_service.messaging.websocket;

import java.util.Objects;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woundex.ws_rider_service.application.handler.RiderGpsMessageHandler;

import com.woundex.ws_rider_service.dto.RiderGpsMessage;
@Component
public class RiderWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(RiderWebSocketHandler.class);

    private final SessionRegistry sessions;
    private final RiderGpsMessageHandler gpsHandler;
    private final ObjectMapper mapper;
    private final Executor executor;

    public RiderWebSocketHandler(
            SessionRegistry sessions,
            RiderGpsMessageHandler gpsHandler,
            ObjectMapper mapper,
            Executor executor
    ) {
        this.sessions = Objects.requireNonNull(sessions, "sessions must not be null");
        this.gpsHandler = Objects.requireNonNull(gpsHandler, "gpsHandler must not be null");
        this.mapper = Objects.requireNonNull(mapper, "mapper must not be null");
        this.executor = Objects.requireNonNull(executor, "executor must not be null");
    }



    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Rider connected: {}", session.getId());
        sessions.register(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Rider disconnected: {}", session.getId());
        sessions.unregister(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        final String payload = message.getPayload();
        RiderGpsMessage gps;
        try {
            gps = parse(payload);
        } catch (Exception e) {
            log.warn("Failed to parse message from session {}: {}", session.getId(), e.getMessage());
            sendError(session, "invalid_message");
            return;
        }

        executor.execute(() -> {
            try {
                gpsHandler.handle(gps);
                sendAck(session);
            } catch (Exception e) {
                log.error("Error handling gps for session {}: {}", session.getId(), e.getMessage(), e);
                sendError(session, "internal_error");
            }
        });
    }

     private RiderGpsMessage parse(String payload) throws Exception {
        return mapper.readValue(payload, RiderGpsMessage.class);
    }

    private void sendAck(WebSocketSession session) {
        try { session.sendMessage(new TextMessage("{\"status\":\"ok\"}")); }
        catch (Exception ignored) { /* ignore send errors */ }
    }

    private void sendError(WebSocketSession session, String code) {
        try { session.sendMessage(new TextMessage("{\"status\":\"error\",\"code\":\"" + code + "\"}")); }
        catch (Exception ignored) { /* ignore send errors */ }
    }

}