package com.woundex.trip_service.domain.commands;

import com.woundex.trip_service.domain.value_object.TripId;

public record CompleteTripCommand(
    TripId tripId
) {
    public CompleteTripCommand {
        if (tripId == null) {
            throw new IllegalArgumentException("TripId Required");
        }
    }
    
}
