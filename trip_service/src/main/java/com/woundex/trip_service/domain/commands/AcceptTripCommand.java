package com.woundex.trip_service.domain.commands;

import com.woundex.trip_service.domain.value_object.DriverId;
import com.woundex.trip_service.domain.value_object.TripId;

public record AcceptTripCommand(
    TripId tripId,
    DriverId driverId
) {}
