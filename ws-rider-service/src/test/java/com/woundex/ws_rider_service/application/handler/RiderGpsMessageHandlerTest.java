package com.woundex.ws_rider_service.application.handler;

import com.woundex.ws_rider_service.application.port.EventPublisher;
import com.woundex.ws_rider_service.domain.Event.RiderLocationUpdatedEvent;
import com.woundex.ws_rider_service.dto.RiderGpsMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RiderGpsMessageHandlerTest {

    @Test
    void handle_publishes_RiderLocationUpdatedEvent_with_correct_payload() {
        EventPublisher publisher = mock(EventPublisher.class);
        RiderGpsMessageHandler handler = new RiderGpsMessageHandler(publisher);

        RiderGpsMessage msg = new RiderGpsMessage("b9f0b9e7-2f3f-4f7a-9b1b-1234567890ab", 12.34, 56.78);

        handler.handle(msg);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher, times(1)).publish(captor.capture());
        Object published = captor.getValue();
        assertTrue(published instanceof RiderLocationUpdatedEvent, "Published event type");
        RiderLocationUpdatedEvent evt = (RiderLocationUpdatedEvent) published;
        assertEquals("b9f0b9e7-2f3f-4f7a-9b1b-1234567890ab", evt.riderId().toString());
        assertEquals(12.34, evt.location().lat());
        assertEquals(56.78, evt.location().lng());
        assertNotNull(evt.occurredAt());
    }
}
