package com.woundex.ws_driver_service.messaging.kafka;

import com.woundex.ws_driver_service.application.handler.TripEventHandler;
import com.woundex.ws_driver_service.domain.event.TripAssignedEvent;
import com.woundex.ws_driver_service.domain.event.TripLifecycleEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for trip events (assignments, status updates).
 */
@Component
public class TripEventConsumer {

    private final TripEventHandler tripEventHandler;

    public TripEventConsumer(TripEventHandler tripEventHandler) {
        this.tripEventHandler = tripEventHandler;
    }

    @KafkaListener(topics = "trip-events")
    public void consume(TripLifecycleEvent event) {
        tripEventHandler.handle(event);
    }

    @KafkaListener(topics = "trip-assignments")
    public void consumeAssignment(TripAssignedEvent event) {
        tripEventHandler.handle(event);
    }
}
