package com.woundex.ws_driver_service.application.handler;

import com.woundex.ws_driver_service.application.port.EventPublisher;
import com.woundex.ws_driver_service.domain.event.DriverLocationUpdatedEvent;
import com.woundex.ws_driver_service.dto.DriverGpsMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DriverGpsMessageHandlerTest {

    @Test
    void handle_publishes_DriverLocationUpdatedEvent_with_correct_payload() {
        EventPublisher publisher = mock(EventPublisher.class);
        DriverGpsMessageHandler handler = new DriverGpsMessageHandler(publisher);

        Instant timestamp = Instant.now();
        DriverGpsMessage msg = new DriverGpsMessage(
                "b9f0b9e7-2f3f-4f7a-9b1b-1234567890ab",
                12.34, 56.78,
                90.0, 45.5,
                timestamp
        );

        handler.handle(msg);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher, times(1)).publish(captor.capture());
        
        Object published = captor.getValue();
        assertTrue(published instanceof DriverLocationUpdatedEvent, "Published event type");
        
        DriverLocationUpdatedEvent evt = (DriverLocationUpdatedEvent) published;
        assertEquals("b9f0b9e7-2f3f-4f7a-9b1b-1234567890ab", evt.driverId().toString());
        assertEquals(12.34, evt.location().lat());
        assertEquals(56.78, evt.location().lng());
        assertEquals(90.0, evt.heading());
        assertEquals(45.5, evt.speed());
        assertEquals(timestamp, evt.timestamp());
        assertNotNull(evt.occurredAt());
    }
}
