package com.woundex.trip_service.domain.commands;

import com.woundex.trip_service.domain.value_object.TripId;

public record CancelTripCommand(
    TripId tripId
) {
    public CancelTripCommand {
        if (tripId == null) {
            throw new IllegalArgumentException("TripId Required");
        }
    }
    
}
