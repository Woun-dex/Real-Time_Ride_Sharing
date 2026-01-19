package com.woundex.ws_driver_service.dto;

import com.woundex.ws_driver_service.domain.event.TripAssignedEvent;

/**
 * DTO for push messages sent to the driver client via WebSocket.
 */
public class DriverPushMessage {
    private String driverId;
    private String type;
    private String payload;

    public DriverPushMessage() {}

    public DriverPushMessage(String driverId, String type, String payload) {
        this.driverId = driverId;
        this.type = type;
        this.payload = payload;
    }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public static DriverPushMessage from(TripAssignedEvent event) {
        return new DriverPushMessage(
                event.driverId().toString(),
                "TRIP_ASSIGNED",
                "You have been assigned to trip: " + event.tripId().toString()
        );
    }

    public static DriverPushMessage tripStatus(String driverId, String tripId, String status) {
        return new DriverPushMessage(
                driverId,
                "TRIP_STATUS",
                "Trip " + tripId + " status: " + status
        );
    }
}
