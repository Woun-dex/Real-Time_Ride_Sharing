package com.woundex.ws_rider_service.messaging.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.woundex.ws_rider_service.domain.value_object.*;
import com.woundex.ws_rider_service.dto.RiderPushMessage;



@Component
public class SessionRegistry {

    private static final Logger log = LoggerFactory.getLogger(SessionRegistry.class);


    private final Map<RiderId, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper mapper;

    public SessionRegistry(ObjectMapper mapper) {
        this.mapper = mapper;
    }

   public void register(WebSocketSession session) {
        Object raw = session.getAttributes().get("riderId");
        if (raw instanceof RiderId) {
            sessions.put((RiderId) raw, session);
            log.debug("Registered session {} for rider {}", session.getId(), raw);
        } else {
            log.warn("Unable to register session {}: missing or invalid riderId attribute", session.getId());
        }
    }

    // unregister by removing any mapping that references this session
    public void unregister(WebSocketSession session) {
        sessions.entrySet().removeIf(e -> {
            boolean match = e.getValue() != null && e.getValue().getId().equals(session.getId());
            if (match) {
                log.debug("Unregistered session {} for rider {}", session.getId(), e.getKey());
            }
            return match;
        });
    }

     public void push(RiderId riderId, RiderPushMessage msg) {
        WebSocketSession session = sessions.get(riderId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(mapper.writeValueAsString(msg)));
            } catch (IOException e) {
            }
        }
    }
}
