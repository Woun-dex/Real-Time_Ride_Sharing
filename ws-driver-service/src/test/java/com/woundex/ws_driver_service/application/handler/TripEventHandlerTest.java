package com.woundex.ws_driver_service.application.handler;

import com.woundex.ws_driver_service.application.port.PushNotifier;
import com.woundex.ws_driver_service.domain.event.TripAssignedEvent;
import com.woundex.ws_driver_service.domain.value_object.DriverId;
import com.woundex.ws_driver_service.domain.value_object.TripId;
import com.woundex.ws_driver_service.dto.DriverPushMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TripEventHandlerTest {

    @Test
    void handle_assigned_event_pushes_message_to_notifier() {
        PushNotifier notifier = mock(PushNotifier.class);
        TripEventHandler handler = new TripEventHandler(notifier);

        TripAssignedEvent event = new TripAssignedEvent(
                TripId.generate(),
                DriverId.of("8f5a1b5e-9c2d-4aa1-8d34-111111111111"),
                Instant.now()
        );

        handler.handle(event);

        ArgumentCaptor<DriverPushMessage> msgCaptor = ArgumentCaptor.forClass(DriverPushMessage.class);
        verify(notifier, times(1)).push(msgCaptor.capture());

        DriverPushMessage msg = msgCaptor.getValue();
        assertEquals("8f5a1b5e-9c2d-4aa1-8d34-111111111111", msg.getDriverId());
        assertEquals("TRIP_ASSIGNED", msg.getType());
        assertTrue(msg.getPayload().contains("assigned"));
    }
}
