package com.woundex.matching_service.domain.event;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Consumes trip.requested events produced by trip_service.
 *
 * trip_service serializes as:
 * {
 *   "tripId": "uuid",
 *   "riderId": "uuid",
 *   "pickupLocation": { "lat": 40.7, "lng": -74.0 },
 *   "destination":    { "lat": 40.8, "lng": -73.9 }
 * }
 *
 * We use @JsonProperty setters to unpack the nested objects into flat fields.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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

    /** Unpack nested pickupLocation: { "lat": ..., "lng": ... } */
    @JsonProperty("pickupLocation")
    public void unpackPickupLocation(Map<String, Double> loc) {
        if (loc != null) {
            this.pickupLat = loc.getOrDefault("lat", 0.0);
            this.pickupLon = loc.getOrDefault("lng", 0.0);
        }
    }

    /** Unpack nested destination: { "lat": ..., "lng": ... } */
    @JsonProperty("destination")
    public void unpackDestination(Map<String, Double> loc) {
        if (loc != null) {
            this.dropoffLat = loc.getOrDefault("lat", 0.0);
            this.dropoffLon = loc.getOrDefault("lng", 0.0);
        }
    }
}