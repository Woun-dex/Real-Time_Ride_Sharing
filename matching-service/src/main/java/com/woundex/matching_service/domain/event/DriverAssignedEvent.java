package com.woundex.matching_service.domain.event;

public class DriverAssignedEvent {
    private String tripId;
    private String driverId;
    private String riderId;
    private double driverLat;
    private double driverLon;
    private long timestamp;

    public DriverAssignedEvent() {}

    public DriverAssignedEvent(String tripId, String driverId, String riderId,
                               double driverLat, double driverLon) {
        this.tripId = tripId;
        this.driverId = driverId;
        this.riderId = riderId;
        this.driverLat = driverLat;
        this.driverLon = driverLon;
        this.timestamp = System.currentTimeMillis();
    }

    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public String getRiderId() { return riderId; }
    public void setRiderId(String riderId) { this.riderId = riderId; }

    public double getDriverLat() { return driverLat; }
    public void setDriverLat(double driverLat) { this.driverLat = driverLat; }

    public double getDriverLon() { return driverLon; }
    public void setDriverLon(double driverLon) { this.driverLon = driverLon; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}