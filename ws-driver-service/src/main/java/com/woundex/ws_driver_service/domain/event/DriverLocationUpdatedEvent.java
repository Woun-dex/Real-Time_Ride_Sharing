package com.woundex.ws_driver_service.domain.event;

import com.woundex.ws_driver_service.domain.value_object.DriverId;
import com.woundex.ws_driver_service.domain.value_object.Location;

import java.time.Instant;
import java.util.Objects;

/**
 * Event emitted when a driver's location is updated (high-frequency GPS).
 */
public final class DriverLocationUpdatedEvent {
    private final DriverId driverId;
    private final Location location;
    private final double heading;
    private final double speed;
    private final Instant timestamp;
    private final Instant occurredAt;

    public DriverLocationUpdatedEvent(DriverId driverId, Location location, double heading, double speed, Instant timestamp, Instant occurredAt) {
        this.driverId = Objects.requireNonNull(driverId);
        this.location = Objects.requireNonNull(location);
        this.heading = heading;
        this.speed = speed;
        this.timestamp = Objects.requireNonNull(timestamp);
        this.occurredAt = Objects.requireNonNull(occurredAt);
    }

    public DriverId driverId() { return driverId; }
    public Location location() { return location; }
    public double heading() { return heading; }
    public double speed() { return speed; }
    public Instant timestamp() { return timestamp; }
    public Instant occurredAt() { return occurredAt; }

    @Override
    public String toString() {
        return "DriverLocationUpdatedEvent{" + driverId + "," + location + ",heading=" + heading + ",speed=" + speed + "," + timestamp + "}";
    }
}
