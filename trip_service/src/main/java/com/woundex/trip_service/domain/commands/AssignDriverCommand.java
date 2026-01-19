package com.woundex.trip_service.domain.commands;

import com.woundex.trip_service.domain.value_object.*;

public record AssignDriverCommand(
    TripId tripId,
    DriverId driverId
) {
    public AssignDriverCommand {
        if (tripId == null) {
            throw new IllegalArgumentException("TripId Required");
        }
        if (driverId == null) {
            throw new IllegalArgumentException("DriverId Required");
        }
    }
    
}
