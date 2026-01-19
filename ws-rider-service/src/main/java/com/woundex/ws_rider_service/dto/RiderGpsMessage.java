package com.woundex.ws_rider_service.dto;

public class RiderGpsMessage {
    private String riderId;
    private double lat;
    private double lng;

    public RiderGpsMessage() {}

    public RiderGpsMessage(String riderId, double lat, double lng) {
        this.riderId = riderId; this.lat = lat; this.lng = lng;
    }

    public String getRiderId() { return riderId; }
    public void setRiderId(String riderId) { this.riderId = riderId; }
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }
}