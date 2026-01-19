package com.woundex.ws_driver_service.domain.event;

import com.woundex.ws_driver_service.domain.value_object.DriverId;
import com.woundex.ws_driver_service.domain.value_object.DriverAvailability;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a driver's availability state changes.
 */
public final class DriverAvailabilityChangedEvent {
    private final DriverId driverId;
    private final DriverAvailability previousState;
    private final DriverAvailability newState;
    private final Instant occurredAt;

    public DriverAvailabilityChangedEvent(DriverId driverId, DriverAvailability previousState, DriverAvailability newState, Instant occurredAt) {
        this.driverId = Objects.requireNonNull(driverId);
        this.previousState = previousState;
        this.newState = Objects.requireNonNull(newState);
        this.occurredAt = Objects.requireNonNull(occurredAt);
    }

    public DriverId driverId() { return driverId; }
    public DriverAvailability previousState() { return previousState; }
    public DriverAvailability newState() { return newState; }
    public Instant occurredAt() { return occurredAt; }

    @Override
    public String toString() {
        return "DriverAvailabilityChangedEvent{" + driverId + "," + previousState + "->" + newState + "," + occurredAt + "}";
    }
}
