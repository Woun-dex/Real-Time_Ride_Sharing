package com.woundex.trip_service.domain.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.woundex.trip_service.domain.events.*;
import com.woundex.trip_service.domain.value_object.*;


public final class Trip_Entity {
    private final TripId id;
    private final RiderId riderId;
    private DriverId driverId; // assigned later
    private Location pickup;
    private Location destination;
    private TripStatus status;

    private final List<Object> domainEvents = new ArrayList<>();

    private Trip_Entity(TripId id, RiderId riderId, Location pickup, Location destination, TripStatus status) {
        this.id = Objects.requireNonNull(id, "id");
        this.riderId = Objects.requireNonNull(riderId, "riderId");
        this.pickup = Objects.requireNonNull(pickup, "pickup");
        this.destination = Objects.requireNonNull(destination, "destination");
        if (pickup.equals(destination)) throw new IllegalArgumentException("pickup and destination must differ");
        this.status = Objects.requireNonNull(status, "status");
    }

    public static Trip_Entity create(TripId id, RiderId riderId, Location pickup, Location destination) {
        var trip = new Trip_Entity(id, riderId, pickup, destination, TripStatus.REQUESTED);
        trip.addEvent( new TripRequestedEvent(id, riderId, pickup, destination));
        return trip;
    }

    public void assignDriver(DriverId driverId) {
        if (status != TripStatus.REQUESTED) throw new IllegalStateException("Trip not assignable");
        this.driverId = Objects.requireNonNull(driverId, "driverId");
        this.status = TripStatus.ASSIGNED;
        addEvent( new DriverAssignedEvent(id, driverId));
    }

    public void start() {
        if (status != TripStatus.ASSIGNED) throw new IllegalStateException("Trip not started");
        if (driverId == null) throw new IllegalStateException("No driver assigned");
        this.status = TripStatus.IN_PROGRESS;
        addEvent( new TripStartedEvent(id, driverId));
    }

    public void complete() {
        if (status != TripStatus.IN_PROGRESS) throw new IllegalStateException("Trip not completable");
        this.status = TripStatus.COMPLETED;
        addEvent( new DriverCompletedEvent(id, driverId));
    }

    public void cancel() {
        if (status == TripStatus.COMPLETED) throw new IllegalStateException("Trip not cancellable");
        this.status = TripStatus.CANCELLED;
        addEvent( new TripCancelledEvent(id));
    }

    private void addEvent(Object event){
        domainEvents.add(event);
    }
    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearEvents() {
        domainEvents.clear();
    }

    
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

