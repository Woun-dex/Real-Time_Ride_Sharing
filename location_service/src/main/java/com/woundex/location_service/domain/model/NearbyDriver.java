package com.woundex.location_service.domain.model;

import java.util.UUID;


public final class NearbyDriver {
    private final UUID driverId;
    private final double distanceMeters;

    public NearbyDriver(UUID driverId, double distanceMeters) {
        this.driverId = driverId;
        this.distanceMeters = distanceMeters;
    }

    public UUID getDriverId() { return driverId; }
    public double getDistanceMeters() { return distanceMeters; }
}