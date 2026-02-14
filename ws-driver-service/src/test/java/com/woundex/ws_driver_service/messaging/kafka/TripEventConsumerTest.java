package com.woundex.ws_driver_service.messaging.kafka;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woundex.ws_driver_service.application.handler.TripEventHandler;
import com.woundex.ws_driver_service.domain.event.TripAssignedEvent;
import com.woundex.ws_driver_service.domain.event.TripLifecycleEvent;
import com.woundex.ws_driver_service.domain.value_object.TripId;

class TripEventConsumerTest {

    @Test
    void consume_delegates_lifecycle_event_to_handler() {
        TripEventHandler handler = mock(TripEventHandler.class);
        ObjectMapper mapper = new ObjectMapper();
        TripEventConsumer consumer = new TripEventConsumer(handler, mapper);

        String tripId = TripId.generate().toString();
        String json = "{\"tripId\":\"" + tripId + "\"}";

        consumer.consume(json, "STARTED");

        verify(handler, times(1)).handle(any(TripLifecycleEvent.class));
    }

    @Test
    void consumeAssignment_delegates_assigned_event_to_handler() {
        TripEventHandler handler = mock(TripEventHandler.class);
        ObjectMapper mapper = new ObjectMapper();
        TripEventConsumer consumer = new TripEventConsumer(handler, mapper);

        String tripId = TripId.generate().toString();
        String driverId = UUID.randomUUID().toString();
        String json = "{\"tripId\":\"" + tripId + "\",\"driverId\":\"" + driverId + "\"}";

        consumer.consumeAssignment(json);

        verify(handler, times(1)).handle(any(TripAssignedEvent.class));
    }
}
