package com.woundex.trip_service.domain.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.woundex.trip_service.domain.events.DriverAssignedEvent;
import com.woundex.trip_service.domain.events.DriverCompletedEvent;
import com.woundex.trip_service.domain.events.TripCancelledEvent;
import com.woundex.trip_service.domain.events.TripRequestedEvent;
import com.woundex.trip_service.domain.events.TripStartedEvent;
import com.woundex.trip_service.domain.value_object.DriverId;
import com.woundex.trip_service.domain.value_object.Location;
import com.woundex.trip_service.domain.value_object.RiderId;
import com.woundex.trip_service.domain.value_object.TripId;
import com.woundex.trip_service.domain.value_object.TripStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;


@Entity
@Table(name = "trips")
public class Trip_Entity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "rider_id", nullable = false)
    private UUID riderId;

    @Column(name = "driver_id")
    private UUID driverId;

    @Column(name = "pickup_lat", nullable = false)
    private double pickupLat;

    @Column(name = "pickup_lng", nullable = false)
    private double pickupLng;

    @Column(name = "destination_lat", nullable = false)
    private double destinationLat;

    @Column(name = "destination_lng", nullable = false)
    private double destinationLng;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TripStatus status;

    @Transient
    private final List<Object> domainEvents = new ArrayList<>();

    protected Trip_Entity() {
        // JPA requires a no-arg constructor
    }

    private Trip_Entity(TripId tripId, RiderId rider, Location pickup, Location destination, TripStatus status) {
        Objects.requireNonNull(tripId, "id");
        Objects.requireNonNull(rider, "riderId");
        Objects.requireNonNull(pickup, "pickup");
        Objects.requireNonNull(destination, "destination");
        if (pickup.equals(destination)) throw new IllegalArgumentException("pickup and destination must differ");
        Objects.requireNonNull(status, "status");

        this.id = tripId.value();
        this.riderId = rider.value();
        this.pickupLat = pickup.lat();
        this.pickupLng = pickup.lng();
        this.destinationLat = destination.lat();
        this.destinationLng = destination.lng();
        this.status = status;
    }

    public static Trip_Entity create(TripId id, RiderId riderId, Location pickup, Location destination) {
        var trip = new Trip_Entity(id, riderId, pickup, destination, TripStatus.REQUESTED);
        trip.addEvent( new TripRequestedEvent(id, riderId, pickup, destination));
        return trip;
    }

    public void assignDriver(DriverId driver) {
        if (status != TripStatus.REQUESTED) throw new IllegalStateException("Trip not assignable");
        Objects.requireNonNull(driver, "driverId");
        this.driverId = driver.value();
        this.status = TripStatus.ASSIGNED;
        addEvent( new DriverAssignedEvent(getId(), driver));
    }

    public void start() {
        if (status != TripStatus.ASSIGNED) throw new IllegalStateException("Trip not started");
        if (driverId == null) throw new IllegalStateException("No driver assigned");
        this.status = TripStatus.IN_PROGRESS;
        addEvent( new TripStartedEvent(getId(), getDriverId().orElseThrow()));
    }

    public void complete() {
        if (status != TripStatus.IN_PROGRESS) throw new IllegalStateException("Trip not completable");
        this.status = TripStatus.COMPLETED;
        addEvent( new DriverCompletedEvent(getId(), getDriverId().orElseThrow()));
    }

    public void cancel() {
        if (status == TripStatus.COMPLETED) throw new IllegalStateException("Trip not cancellable");
        this.status = TripStatus.CANCELLED;
        addEvent( new TripCancelledEvent(getId()));
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

    public TripId getId() { return TripId.of(id.toString()); }
    public RiderId getRiderId() { return RiderId.of(riderId); }
    public Optional<DriverId> getDriverId() { return driverId == null ? Optional.empty() : Optional.of(DriverId.of(driverId)); }
    public Location getPickup() { return new Location(pickupLat, pickupLng); }
    public Location getDestination() { return new Location(destinationLat, destinationLng); }
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

