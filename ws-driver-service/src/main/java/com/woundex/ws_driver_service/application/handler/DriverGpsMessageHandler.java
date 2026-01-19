package com.woundex.ws_driver_service.application.handler;

import com.woundex.ws_driver_service.application.port.EventPublisher;
import com.woundex.ws_driver_service.domain.event.DriverLocationUpdatedEvent;
import com.woundex.ws_driver_service.domain.value_object.DriverId;
import com.woundex.ws_driver_service.domain.value_object.Location;
import com.woundex.ws_driver_service.dto.DriverGpsMessage;

import java.time.Instant;
import java.util.Objects;

/**
 * Handles incoming GPS messages from driver clients and publishes location events.
 */
public class DriverGpsMessageHandler {

    private final EventPublisher publisher;

    public DriverGpsMessageHandler(EventPublisher publisher) {
        this.publisher = Objects.requireNonNull(publisher, "publisher must not be null");
    }

    public void handle(DriverGpsMessage msg) {
        Objects.requireNonNull(msg, "msg must not be null");

        DriverLocationUpdatedEvent event = new DriverLocationUpdatedEvent(
                DriverId.of(msg.getDriverId()),
                new Location(msg.getLat(), msg.getLng()),
                msg.getHeading(),
                msg.getSpeed(),
                msg.getTimestamp() != null ? msg.getTimestamp() : Instant.now(),
                Instant.now()
        );

        publisher.publish(event);
    }
}
