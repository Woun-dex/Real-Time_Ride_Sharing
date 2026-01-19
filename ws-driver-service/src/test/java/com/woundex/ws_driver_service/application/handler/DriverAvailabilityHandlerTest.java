package com.woundex.ws_driver_service.application.handler;

import com.woundex.ws_driver_service.application.port.DriverStateStore;
import com.woundex.ws_driver_service.application.port.EventPublisher;
import com.woundex.ws_driver_service.domain.event.DriverAvailabilityChangedEvent;
import com.woundex.ws_driver_service.domain.value_object.DriverAvailability;
import com.woundex.ws_driver_service.domain.value_object.DriverId;
import com.woundex.ws_driver_service.dto.DriverAvailabilityMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DriverAvailabilityHandlerTest {

    @Test
    void handle_publishes_event_when_state_changes() {
        DriverStateStore stateStore = mock(DriverStateStore.class);
        EventPublisher publisher = mock(EventPublisher.class);
        DriverAvailabilityHandler handler = new DriverAvailabilityHandler(stateStore, publisher);

        String driverId = "b9f0b9e7-2f3f-4f7a-9b1b-1234567890ab";
        when(stateStore.getAvailability(any())).thenReturn(DriverAvailability.OFFLINE);

        DriverAvailabilityMessage msg = new DriverAvailabilityMessage(driverId, DriverAvailability.AVAILABLE);
        handler.handle(msg);

        verify(stateStore).setAvailability(any(), eq(DriverAvailability.AVAILABLE));

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher).publish(captor.capture());

        Object published = captor.getValue();
        assertTrue(published instanceof DriverAvailabilityChangedEvent);

        DriverAvailabilityChangedEvent evt = (DriverAvailabilityChangedEvent) published;
        assertEquals(driverId, evt.driverId().toString());
        assertEquals(DriverAvailability.OFFLINE, evt.previousState());
        assertEquals(DriverAvailability.AVAILABLE, evt.newState());
    }

    @Test
    void handle_does_not_publish_when_state_unchanged() {
        DriverStateStore stateStore = mock(DriverStateStore.class);
        EventPublisher publisher = mock(EventPublisher.class);
        DriverAvailabilityHandler handler = new DriverAvailabilityHandler(stateStore, publisher);

        String driverId = "b9f0b9e7-2f3f-4f7a-9b1b-1234567890ab";
        when(stateStore.getAvailability(any())).thenReturn(DriverAvailability.AVAILABLE);

        DriverAvailabilityMessage msg = new DriverAvailabilityMessage(driverId, DriverAvailability.AVAILABLE);
        handler.handle(msg);

        verify(publisher, never()).publish(any());
    }
}
