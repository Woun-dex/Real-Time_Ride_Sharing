package com.woundex.ws_driver_service.messaging.kafka;

import com.woundex.ws_driver_service.application.handler.TripEventHandler;
import com.woundex.ws_driver_service.domain.event.TripAssignedEvent;
import com.woundex.ws_driver_service.domain.event.TripLifecycleEvent;
import com.woundex.ws_driver_service.domain.value_object.DriverId;
import com.woundex.ws_driver_service.domain.value_object.TripId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.mockito.Mockito.*;

class TripEventConsumerTest {

    @Test
    void consume_delegates_lifecycle_event_to_handler() {
        TripEventHandler handler = mock(TripEventHandler.class);
        TripEventConsumer consumer = new TripEventConsumer(handler);

        TripLifecycleEvent event = new TripLifecycleEvent(TripId.generate(), "STARTED", Instant.now());
        consumer.consume(event);

        verify(handler, times(1)).handle(event);
    }

    @Test
    void consumeAssignment_delegates_assigned_event_to_handler() {
        TripEventHandler handler = mock(TripEventHandler.class);
        TripEventConsumer consumer = new TripEventConsumer(handler);

        TripAssignedEvent event = new TripAssignedEvent(TripId.generate(), DriverId.generate(), Instant.now());
        consumer.consumeAssignment(event);

        verify(handler, times(1)).handle(event);
    }
}
