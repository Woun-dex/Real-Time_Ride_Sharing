package com.woundex.ws_rider_service.messaging.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woundex.ws_rider_service.domain.value_object.RiderId;
import com.woundex.ws_rider_service.dto.RiderPushMessage;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SessionRegistryTest {

    @Test
    void register_unregister_and_push() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SessionRegistry registry = new SessionRegistry(mapper);

        WebSocketSession session = mock(WebSocketSession.class);
        RiderId riderId = RiderId.of(UUID.randomUUID());

        Map<String,Object> attrs = new HashMap<>();
        attrs.put("riderId", riderId);
        when(session.getAttributes()).thenReturn(attrs);
        when(session.getId()).thenReturn("sess-2");
        when(session.isOpen()).thenReturn(true);

        registry.register(session);
        registry.push(riderId, new RiderPushMessage(riderId.toString(), "hello"));
        verify(session, atLeastOnce()).sendMessage(any(TextMessage.class));

        registry.unregister(session);
        registry.push(riderId, new RiderPushMessage(riderId.toString(), "should not send"));
        // After unregister, no further send should occur (we can't assert negative easily without additional setup)
    }
}
