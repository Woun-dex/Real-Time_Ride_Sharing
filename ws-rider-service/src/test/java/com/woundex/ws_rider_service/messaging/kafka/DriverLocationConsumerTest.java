package com.woundex.ws_rider_service.messaging.kafka;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woundex.ws_rider_service.application.port.PushNotifier;
import com.woundex.ws_rider_service.domain.Event.RiderLocationUpdatedEvent;

public class DriverLocationConsumerTest {

    @Test
    void onDriverLocation_pushes_to_notifier() {
        PushNotifier notifier = mock(PushNotifier.class);
        ObjectMapper mapper = new ObjectMapper();
        DriverLocationConsumer consumer = new DriverLocationConsumer(notifier, mapper);

        String riderId = UUID.randomUUID().toString();
        String json = "{\"riderId\":\"" + riderId + "\", \"location\":{\"lat\":10,\"lng\":20}}";

        consumer.onDriverLocation(json);

        verify(notifier, times(1)).pushForDriverLocation(any(RiderLocationUpdatedEvent.class));
    }
}
