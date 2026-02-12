package com.woundex.matching_service.matching;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class PendingOfferStore {

    private final ConcurrentHashMap<String, PendingOffer> offers = new ConcurrentHashMap<>();

    public void put(String tripId, PendingOffer offer) {
        offers.put(tripId, offer);
    }

    public PendingOffer get(String tripId) {
        return offers.get(tripId);
    }

    public PendingOffer remove(String tripId) {
        return offers.remove(tripId);
    }

    public Map<String, PendingOffer> getAll() {
        return offers;
    }
}