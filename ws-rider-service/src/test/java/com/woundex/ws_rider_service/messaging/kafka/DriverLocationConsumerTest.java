package com.woundex.ws_rider_service.messaging.kafka;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woundex.ws_rider_service.messaging.websocket.SessionRegistry;

public class DriverLocationConsumerTest {

    @Test
    void onDriverLocation_pushes_to_registry() {
        SessionRegistry registry = mock(SessionRegistry.class);
        ObjectMapper mapper = new ObjectMapper();
        DriverLocationConsumer consumer = new DriverLocationConsumer(registry, mapper);

        String driverId = UUID.randomUUID().toString();
        String tripId = UUID.randomUUID().toString();
        String json = "{\"driverId\":\"" + driverId + "\", \"lat\":10, \"lon\":20, \"tripId\":\"" + tripId + "\"}";

        consumer.onDriverLocation(json);

        verify(registry, times(1)).pushByTripId(any(String.class), any(String.class));
    }
}
