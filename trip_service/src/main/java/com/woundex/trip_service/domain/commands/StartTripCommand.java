package com.woundex.trip_service.domain.commands;

import java.sql.Driver;

import com.woundex.trip_service.domain.value_object.DriverId;
import com.woundex.trip_service.domain.value_object.TripId;

public record StartTripCommand(
    TripId tripId,
    DriverId driverId
) {
    public StartTripCommand {
        if (tripId == null) {
            throw new IllegalArgumentException("TripId Required");
        }
        if (driverId == null) {
            throw new IllegalArgumentException("DriverId Required");
        }
    }
    
}
