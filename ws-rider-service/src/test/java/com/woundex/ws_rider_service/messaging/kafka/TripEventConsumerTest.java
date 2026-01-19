package com.woundex.ws_rider_service.messaging.kafka;

import com.woundex.ws_rider_service.application.handler.TripEventHandler;
import com.woundex.ws_rider_service.domain.Event.TripLifecycleEvent;
import com.woundex.ws_rider_service.domain.value_object.TripId;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import java.time.Instant;

public class TripEventConsumerTest {

    @Test
    void consume_delegates_to_handler_with_trip_lifecycle_event() {
        TripEventHandler handler = mock(TripEventHandler.class);
        TripEventConsumer consumer = new TripEventConsumer(handler);

        TripLifecycleEvent event = new TripLifecycleEvent(TripId.generate(), "ASSIGNED", Instant.now());
        consumer.consume(event);

        verify(handler, times(1)).handle(event);
    }
}
