package com.woundex.trip_service.domain.commands;

import com.woundex.trip_service.domain.value_object.DriverId;
import com.woundex.trip_service.domain.value_object.TripId;
import com.woundex.trip_service.domain.value_object.TripStatus;

public record UpdateStatusCommand(
    TripId tripId,
    TripStatus status,
    DriverId driverId
) {}
