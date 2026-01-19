package com.woundex.ws_rider_service.domain.Event;

import java.time.Instant;
import java.util.Objects;

import com.woundex.ws_rider_service.domain.value_object.RiderId;
import com.woundex.ws_rider_service.domain.value_object.TripId;

public record TripAssignedEvent(
    TripId tripId,
    RiderId riderId,
    Instant occurredAt
) {
    public TripAssignedEvent {
        Objects.requireNonNull(tripId, "tripId must not be null");
        Objects.requireNonNull(riderId, "riderId must not be null");
        Objects.requireNonNull(occurredAt, "occurredAt must not be null");
    }
}