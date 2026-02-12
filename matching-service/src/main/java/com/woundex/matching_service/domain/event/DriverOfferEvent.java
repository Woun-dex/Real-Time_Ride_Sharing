package com.woundex.matching_service.domain.event;

public class DriverOfferEvent {
    private String tripId;
    private String driverId;
    private String riderId;
    private double pickupLat;
    private double pickupLon;
    private double dropoffLat;
    private double dropoffLon;
    private long expiresAt;

    public DriverOfferEvent() {}

    public DriverOfferEvent(String tripId, String driverId, String riderId,
                            double pickupLat, double pickupLon,
                            double dropoffLat, double dropoffLon,
                            long expiresAt) {
        this.tripId = tripId;
        this.driverId = driverId;
        this.riderId = riderId;
        this.pickupLat = pickupLat;
        this.pickupLon = pickupLon;
        this.dropoffLat = dropoffLat;
        this.dropoffLon = dropoffLon;
        this.expiresAt = expiresAt;
    }

    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }

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

    public long getExpiresAt() { return expiresAt; }
    public void setExpiresAt(long expiresAt) { this.expiresAt = expiresAt; }
}