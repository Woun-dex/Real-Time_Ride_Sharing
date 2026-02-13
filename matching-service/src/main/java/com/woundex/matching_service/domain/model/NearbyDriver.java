// filepath: c:\Users\Woundex\Documents\Project\Ride sharing App\matching_service\src\main\java\com\woundex\matching_service\domain\model\NearbyDriver.java
package com.woundex.matching_service.domain.model;

public class NearbyDriver {
    private String driverId;
    private Position position;
    private double distanceMeters;

    public NearbyDriver() {}

    public NearbyDriver(String driverId, Position position, double distanceMeters) {
        this.driverId = driverId;
        this.position = position;
        this.distanceMeters = distanceMeters;
    }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }

    public double getDistanceMeters() { return distanceMeters; }
    public void setDistanceMeters(double distanceMeters) { this.distanceMeters = distanceMeters; }
}