package com.woundex.matching_service.matching;

import java.util.HashSet;
import java.util.Set;

import com.woundex.matching_service.domain.event.TripRequestedEvent;
import com.woundex.matching_service.domain.model.NearbyDriver;

public class PendingOffer {
    private final String tripId;
    private final String driverId;
    private final TripRequestedEvent tripEvent;
    private final NearbyDriver driver;
    private final long expiresAt;
    private final Set<String> triedDriverIds;

    public PendingOffer(String tripId, String driverId, TripRequestedEvent tripEvent,
                        NearbyDriver driver, long expiresAt, Set<String> triedDriverIds) {
        this.tripId = tripId;
        this.driverId = driverId;
        this.tripEvent = tripEvent;
        this.driver = driver;
        this.expiresAt = expiresAt;
        this.triedDriverIds = new HashSet<>(triedDriverIds);
    }

    public String getTripId() { return tripId; }
    public String getDriverId() { return driverId; }
    public TripRequestedEvent getTripEvent() { return tripEvent; }
    public NearbyDriver getDriver() { return driver; }
    public long getExpiresAt() { return expiresAt; }
    public Set<String> getTriedDriverIds() { return triedDriverIds; }
    public boolean isExpired() { return System.currentTimeMillis() > expiresAt; }
}