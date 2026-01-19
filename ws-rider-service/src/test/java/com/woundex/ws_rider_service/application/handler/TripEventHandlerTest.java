package com.woundex.ws_rider_service.application.handler;

import com.woundex.ws_rider_service.application.port.PushNotifier;
import com.woundex.ws_rider_service.domain.Event.TripAssignedEvent;
import com.woundex.ws_rider_service.domain.value_object.RiderId;
import com.woundex.ws_rider_service.domain.value_object.TripId;
import com.woundex.ws_rider_service.dto.RiderPushMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TripEventHandlerTest {

    @Test
    void handle_assigned_event_pushes_message_to_notifier() {
        PushNotifier notifier = mock(PushNotifier.class);
        TripEventHandler handler = new TripEventHandler(notifier);

        TripAssignedEvent event = new TripAssignedEvent(TripId.generate(), RiderId.of("8f5a1b5e-9c2d-4aa1-8d34-111111111111"), Instant.now());

        handler.handle(event);

        ArgumentCaptor<RiderPushMessage> msgCaptor = ArgumentCaptor.forClass(RiderPushMessage.class);
        verify(notifier, times(1)).push(msgCaptor.capture());
        RiderPushMessage msg = msgCaptor.getValue();
        assertEquals("8f5a1b5e-9c2d-4aa1-8d34-111111111111", msg.getRiderId());
        assertTrue(msg.getPayload().contains("assigned"));
    }
}
