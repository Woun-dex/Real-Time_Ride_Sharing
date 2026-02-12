package com.woundex.matching_service.domain.event;

public class DriverAcceptedEvent {
    private String tripId;
    private String driverId;

    public DriverAcceptedEvent() {}

    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
}