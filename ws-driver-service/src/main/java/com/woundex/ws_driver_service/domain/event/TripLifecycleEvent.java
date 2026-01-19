package com.woundex.ws_driver_service.domain.event;

import com.woundex.ws_driver_service.domain.value_object.TripId;

import java.time.Instant;
import java.util.Objects;

/**
 * Generic trip lifecycle event received from Kafka.
 */
public final class TripLifecycleEvent {
    private final TripId tripId;
    private final String type;
    private final Instant occurredAt;

    public TripLifecycleEvent(TripId tripId, String type, Instant occurredAt) {
        this.tripId = Objects.requireNonNull(tripId);
        this.type = Objects.requireNonNull(type);
        this.occurredAt = Objects.requireNonNull(occurredAt);
    }

    public TripId tripId() { return tripId; }
    public String type() { return type; }
    public Instant occurredAt() { return occurredAt; }

    @Override
    public String toString() {
        return "TripLifecycleEvent{" + tripId + "," + type + "," + occurredAt + "}";
    }
}
