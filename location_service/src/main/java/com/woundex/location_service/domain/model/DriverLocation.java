package main.java.com.woundex.location_service.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;


public final class DriverLocation {
    private final UUID driverId;
    private final Position position;
    private final Instant timestamp;

    public DriverLocation(UUID driverId, Position position, Instant timestamp) {
        this.driverId = Objects.requireNonNull(driverId, "driverId");
        this.position = Objects.requireNonNull(position, "position");
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
    }

    public UUID getDriverId() { return driverId; }
    public Position getPosition() { return position; }
    public Instant getTimestamp() { return timestamp; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DriverLocation)) return false;
        DriverLocation that = (DriverLocation) o;
        return driverId.equals(that.driverId) && position.equals(that.position) && timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverId, position, timestamp);
    }

    @Override
    public String toString() {
        return "DriverLocation{" + "driverId=" + driverId + ", position=" + position + ", timestamp=" + timestamp + '}';
    }
}