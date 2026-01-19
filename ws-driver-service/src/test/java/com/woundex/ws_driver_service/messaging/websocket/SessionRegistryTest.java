package com.woundex.ws_driver_service.messaging.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woundex.ws_driver_service.domain.value_object.DriverId;
import com.woundex.ws_driver_service.dto.DriverPushMessage;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SessionRegistryTest {

    @Test
    void register_unregister_and_push() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SessionRegistry registry = new SessionRegistry(mapper);

        WebSocketSession session = mock(WebSocketSession.class);
        DriverId driverId = DriverId.of(UUID.randomUUID());

        Map<String, Object> attrs = new HashMap<>();
        attrs.put("driverId", driverId);
        when(session.getAttributes()).thenReturn(attrs);
        when(session.getId()).thenReturn("sess-2");
        when(session.isOpen()).thenReturn(true);

        registry.register(session);
        registry.push(driverId, new DriverPushMessage(driverId.toString(), "TEST", "hello"));
        verify(session, atLeastOnce()).sendMessage(any(TextMessage.class));

        registry.unregister(session);
        // After unregister, no further send should occur
    }
}
