package com.woundex.trip_service.domain.events;

import com.woundex.trip_service.domain.value_object.DriverId;
import com.woundex.trip_service.domain.value_object.TripId;

public record DriverAssignedEvent(
    TripId tripId,
    DriverId driverId
) {}