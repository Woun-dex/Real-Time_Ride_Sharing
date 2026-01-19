package com.woundex.ws_driver_service.application.handler;

import com.woundex.ws_driver_service.application.port.DriverStateStore;
import com.woundex.ws_driver_service.application.port.EventPublisher;
import com.woundex.ws_driver_service.domain.event.DriverAvailabilityChangedEvent;
import com.woundex.ws_driver_service.domain.value_object.DriverAvailability;
import com.woundex.ws_driver_service.domain.value_object.DriverId;
import com.woundex.ws_driver_service.dto.DriverAvailabilityMessage;

import java.time.Instant;
import java.util.Objects;

/**
 * Handles driver availability state changes.
 */
public class DriverAvailabilityHandler {

    private final DriverStateStore stateStore;
    private final EventPublisher publisher;

    public DriverAvailabilityHandler(DriverStateStore stateStore, EventPublisher publisher) {
        this.stateStore = Objects.requireNonNull(stateStore, "stateStore must not be null");
        this.publisher = Objects.requireNonNull(publisher, "publisher must not be null");
    }

    public void handle(DriverAvailabilityMessage msg) {
        Objects.requireNonNull(msg, "msg must not be null");

        DriverId driverId = DriverId.of(msg.getDriverId());
        DriverAvailability previousState = stateStore.getAvailability(driverId);
        DriverAvailability newState = msg.getAvailability();

        if (previousState != newState) {
            stateStore.setAvailability(driverId, newState);

            DriverAvailabilityChangedEvent event = new DriverAvailabilityChangedEvent(
                    driverId,
                    previousState,
                    newState,
                    Instant.now()
            );

            publisher.publish(event);
        }
    }
}
