package com.woundex.ws_rider_service.messaging.kafka;

import com.woundex.ws_rider_service.application.port.PushNotifier;
import com.woundex.ws_rider_service.domain.Event.RiderLocationUpdatedEvent;
import com.woundex.ws_rider_service.domain.value_object.Location;
import com.woundex.ws_rider_service.domain.value_object.RiderId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class DriverLocationConsumerTest {

    @Test
    void onDriverLocation_pushes_to_notifier() {
        PushNotifier notifier = mock(PushNotifier.class);
        DriverLocationConsumer consumer = new DriverLocationConsumer(notifier);

        RiderLocationUpdatedEvent evt = new RiderLocationUpdatedEvent(
                new RiderId(UUID.randomUUID()), new Location(10, 20), Instant.now());

        consumer.onDriverLocation(evt);

        verify(notifier, times(1)).pushForDriverLocation(evt);
    }
}
