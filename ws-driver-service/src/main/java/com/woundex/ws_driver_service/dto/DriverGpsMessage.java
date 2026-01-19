package com.woundex.ws_driver_service.dto;

import java.time.Instant;

/**
 * DTO for high-frequency GPS updates from the driver client.
 */
public class DriverGpsMessage {
    private String driverId;
    private double lat;
    private double lng;
    private double heading;
    private double speed;
    private Instant timestamp;

    public DriverGpsMessage() {}

    public DriverGpsMessage(String driverId, double lat, double lng, double heading, double speed, Instant timestamp) {
        this.driverId = driverId;
        this.lat = lat;
        this.lng = lng;
        this.heading = heading;
        this.speed = speed;
        this.timestamp = timestamp;
    }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }
    public double getHeading() { return heading; }
    public void setHeading(double heading) { this.heading = heading; }
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
