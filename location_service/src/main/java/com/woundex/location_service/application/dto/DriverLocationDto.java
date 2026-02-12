package com.woundex.location_service.application.dto;

public class DriverLocationDto {
    private String driverId;
    private double lat;
    private double lon;
    private long timestamp; // epoch millis

    public DriverLocationDto() {}

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(double lon) { this.lon = lon; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}