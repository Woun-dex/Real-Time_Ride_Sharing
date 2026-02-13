package com.woundex.matching_service.domain.event;

public class DriverLocationEvent {
    private String driverId;
    private double lat;
    private double lon;

    public DriverLocationEvent() {}

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }
}