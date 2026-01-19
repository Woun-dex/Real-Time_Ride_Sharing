package com.woundex.ws_rider_service.dto;

public class RiderPushMessage {
    private String riderId;
    private String payload;

    public RiderPushMessage() {}
    public RiderPushMessage(String riderId, String payload) { this.riderId = riderId; this.payload = payload; }

    public String getRiderId() { return riderId; }
    public void setRiderId(String riderId) { this.riderId = riderId; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public static RiderPushMessage from(com.woundex.ws_rider_service.domain.Event.TripAssignedEvent event){
        return new RiderPushMessage(
                event.riderId().toString(),
                "You have been assigned to trip: " + event.tripId().toString()
        );
    }
}