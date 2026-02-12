package com.woundex.matching_service.domain.event;

public class DriverExpiredEvent {
    private String tripId;
    private String driverId;
    private String reason;

    public DriverExpiredEvent() {}

    public DriverExpiredEvent(String tripId, String driverId, String reason) {
        this.tripId = tripId;
        this.driverId = driverId;
        this.reason = reason;
    }

    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}