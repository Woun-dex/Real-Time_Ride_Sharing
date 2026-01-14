package com.woundex.trip_service.domain.entities;

import java.util.Objects;
import java.util.Optional;
import com.woundex.trip_service.domain.value_object.*;


public final class Trip_Entity {
    private final TripId id;
    private final RiderId riderId;
    private DriverId driverId; // assigned later
    private Location pickup;
    private Location destination;
    private TripStatus status;

    private Trip_Entity(TripId id, RiderId riderId, Location pickup, Location destination, TripStatus status) {
        this.id = Objects.requireNonNull(id, "id");
        this.riderId = Objects.requireNonNull(riderId, "riderId");
        this.pickup = Objects.requireNonNull(pickup, "pickup");
        this.destination = Objects.requireNonNull(destination, "destination");
        if (pickup.equals(destination)) throw new IllegalArgumentException("pickup and destination must differ");
        this.status = Objects.requireNonNull(status, "status");
    }

    public static Trip_Entity create(TripId id, RiderId riderId, Location pickup, Location destination) {
        return new Trip_Entity(id, riderId, pickup, destination, TripStatus.REQUESTED);
    }

    public void assignDriver(DriverId driverId) {
        if (status != TripStatus.REQUESTED) throw new IllegalStateException("Trip not assignable");
        this.driverId = Objects.requireNonNull(driverId, "driverId");
        this.status = TripStatus.ASSIGNED;
    }

    public void start() {
        if (status != TripStatus.ASSIGNED) throw new IllegalStateException("Trip not started");
        if (driverId == null) throw new IllegalStateException("No driver assigned");
        this.status = TripStatus.IN_PROGRESS;
    }

    public void complete() {
        if (status != TripStatus.IN_PROGRESS) throw new IllegalStateException("Trip not completable");
        this.status = TripStatus.COMPLETED;
    }

    // Getters
    public TripId getId() { return id; }
    public RiderId getRiderId() { return riderId; }
    public Optional<DriverId> getDriverId() { return Optional.ofNullable(driverId); }
    public Location getPickup() { return pickup; }
    public Location getDestination() { return destination; }
    public TripStatus getStatus() { return status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trip_Entity)) return false;
        Trip_Entity other = (Trip_Entity) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Trip{" + "id=" + id + ", rider=" + riderId + ", driver=" + driverId + ", status=" + status + '}';
    }

    

    
}

