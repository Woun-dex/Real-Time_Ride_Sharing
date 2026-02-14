package com.woundex.ws_rider_service.messaging.kafka;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woundex.ws_rider_service.application.handler.TripEventHandler;
import com.woundex.ws_rider_service.domain.Event.TripLifecycleEvent;
import com.woundex.ws_rider_service.domain.value_object.TripId;

public class TripEventConsumerTest {

    @Test
    void consume_delegates_to_handler_with_trip_lifecycle_event() {
        TripEventHandler handler = mock(TripEventHandler.class);
        ObjectMapper mapper = new ObjectMapper();
        TripEventConsumer consumer = new TripEventConsumer(handler, mapper);

        String tripId = TripId.generate().toString();
        String json = "{\"tripId\":\"" + tripId + "\"}";

        consumer.consume(json, "ASSIGNED");

        verify(handler, times(1)).handle(any(TripLifecycleEvent.class));
    }
}
