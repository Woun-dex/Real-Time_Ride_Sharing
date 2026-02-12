package com.woundex.matching_service.domain.event;

public class TripRequestedEvent {
    private String tripId;
    private String riderId;
    private double pickupLat;
    private double pickupLon;
    private double dropoffLat;
    private double dropoffLon;
    private double radiusMeters;
    private int limit;

    public TripRequestedEvent() {}

    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }

    public String getRiderId() { return riderId; }
    public void setRiderId(String riderId) { this.riderId = riderId; }

    public double getPickupLat() { return pickupLat; }
    public void setPickupLat(double pickupLat) { this.pickupLat = pickupLat; }

    public double getPickupLon() { return pickupLon; }
    public void setPickupLon(double pickupLon) { this.pickupLon = pickupLon; }

    public double getDropoffLat() { return dropoffLat; }
    public void setDropoffLat(double dropoffLat) { this.dropoffLat = dropoffLat; }

    public double getDropoffLon() { return dropoffLon; }
    public void setDropoffLon(double dropoffLon) { this.dropoffLon = dropoffLon; }

    public double getRadiusMeters() { return radiusMeters; }
    public void setRadiusMeters(double radiusMeters) { this.radiusMeters = radiusMeters; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
}