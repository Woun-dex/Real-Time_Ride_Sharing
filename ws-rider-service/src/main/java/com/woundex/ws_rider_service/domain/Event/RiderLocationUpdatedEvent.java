package com.woundex.ws_rider_service.domain.Event;

import com.woundex.ws_rider_service.domain.value_object.Location;
import com.woundex.ws_rider_service.domain.value_object.RiderId;

import java.time.Instant;
import java.util.Objects;

public final class RiderLocationUpdatedEvent {
    private final RiderId riderId;
    private final Location location;
    private final Instant occurredAt;

    public RiderLocationUpdatedEvent(RiderId riderId, Location location, Instant occurredAt) {
        this.riderId = Objects.requireNonNull(riderId);
        this.location = Objects.requireNonNull(location);
        this.occurredAt = Objects.requireNonNull(occurredAt);
    }

    public RiderId riderId() { return riderId; }
    public Location location() { return location; }
    public Instant occurredAt() { return occurredAt; }
    @Override public String toString() { return "RiderLocationUpdatedEvent{" + riderId + "," + location + "," + occurredAt + "}"; }
}