package com.woundex.ws_driver_service.domain.event;

import com.woundex.ws_driver_service.domain.value_object.DriverId;
import com.woundex.ws_driver_service.domain.value_object.TripId;

import java.time.Instant;
import java.util.Objects;

/**
 * Event received when a trip is assigned to a driver.
 */
public record TripAssignedEvent(
        TripId tripId,
        DriverId driverId,
        Instant occurredAt
) {
    public TripAssignedEvent {
        Objects.requireNonNull(tripId, "tripId must not be null");
        Objects.requireNonNull(driverId, "driverId must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
    }
}
