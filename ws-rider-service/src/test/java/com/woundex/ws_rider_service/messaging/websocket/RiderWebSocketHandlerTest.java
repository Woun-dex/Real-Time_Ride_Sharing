package com.woundex.ws_rider_service.messaging.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woundex.ws_rider_service.application.handler.RiderGpsMessageHandler;
import com.woundex.ws_rider_service.domain.value_object.RiderId;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

import static org.mockito.Mockito.*;

public class RiderWebSocketHandlerTest {

    @Test
    void connection_registers_and_message_acknowledged_and_delegated() throws Exception {
        SessionRegistry sessions = mock(SessionRegistry.class);
        RiderGpsMessageHandler gpsHandler = mock(RiderGpsMessageHandler.class);
        ObjectMapper mapper = new ObjectMapper();
        Executor executor = Runnable::run; // run tasks immediately

        RiderWebSocketHandler handler = new RiderWebSocketHandler(sessions, gpsHandler, mapper, executor);

        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("sess-1");
        Map<String,Object> attrs = new HashMap<>();
        attrs.put("riderId", RiderId.of(UUID.randomUUID()));
        when(session.getAttributes()).thenReturn(attrs);
        when(session.isOpen()).thenReturn(true);

        handler.afterConnectionEstablished(session);
        verify(sessions, times(1)).register(session);

        String json = "{\"riderId\":\"4f0b8a9e-2c3d-4e5f-8a9b-222222222222\",\"lat\":10.0,\"lng\":20.0}";
        handler.handleTextMessage(session, new TextMessage(json));

        // gps handler called and ack sent
        verify(gpsHandler, times(1)).handle(any());
        verify(session, atLeastOnce()).sendMessage(any(TextMessage.class));

        handler.afterConnectionClosed(session, null);
        verify(sessions, times(1)).unregister(session);
    }
}
