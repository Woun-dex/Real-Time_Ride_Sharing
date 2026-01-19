package com.woundex.ws_driver_service.messaging.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woundex.ws_driver_service.application.handler.DriverAvailabilityHandler;
import com.woundex.ws_driver_service.application.handler.DriverGpsMessageHandler;
import com.woundex.ws_driver_service.domain.value_object.DriverId;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

import static org.mockito.Mockito.*;

class DriverWebSocketHandlerTest {

    @Test
    void connection_registers_and_gps_message_acknowledged_and_delegated() throws Exception {
        SessionRegistry sessions = mock(SessionRegistry.class);
        DriverGpsMessageHandler gpsHandler = mock(DriverGpsMessageHandler.class);
        DriverAvailabilityHandler availabilityHandler = mock(DriverAvailabilityHandler.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules(); // for Instant support
        Executor executor = Runnable::run;

        DriverWebSocketHandler handler = new DriverWebSocketHandler(
                sessions, gpsHandler, availabilityHandler, mapper, executor
        );

        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("sess-1");
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("driverId", DriverId.of(UUID.randomUUID()));
        when(session.getAttributes()).thenReturn(attrs);
        when(session.isOpen()).thenReturn(true);

        handler.afterConnectionEstablished(session);
        verify(sessions, times(1)).register(session);

        String json = "{\"driverId\":\"4f0b8a9e-2c3d-4e5f-8a9b-222222222222\",\"lat\":10.0,\"lng\":20.0,\"heading\":45.0,\"speed\":30.0,\"timestamp\":\"2026-01-20T00:00:00Z\"}";
        handler.handleTextMessage(session, new TextMessage(json));

        verify(gpsHandler, times(1)).handle(any());
        verify(session, atLeastOnce()).sendMessage(any(TextMessage.class));

        handler.afterConnectionClosed(session, null);
        verify(sessions, times(1)).unregister(session);
    }

    @Test
    void availability_message_delegates_to_availability_handler() throws Exception {
        SessionRegistry sessions = mock(SessionRegistry.class);
        DriverGpsMessageHandler gpsHandler = mock(DriverGpsMessageHandler.class);
        DriverAvailabilityHandler availabilityHandler = mock(DriverAvailabilityHandler.class);
        ObjectMapper mapper = new ObjectMapper();
        Executor executor = Runnable::run;

        DriverWebSocketHandler handler = new DriverWebSocketHandler(
                sessions, gpsHandler, availabilityHandler, mapper, executor
        );

        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("sess-2");
        when(session.isOpen()).thenReturn(true);

        String json = "{\"driverId\":\"4f0b8a9e-2c3d-4e5f-8a9b-222222222222\",\"availability\":\"AVAILABLE\"}";
        handler.handleTextMessage(session, new TextMessage(json));

        verify(availabilityHandler, times(1)).handle(any());
        verify(session, atLeastOnce()).sendMessage(any(TextMessage.class));
    }
}
