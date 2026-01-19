package com.woundex.trip_service.domain.events;

import com.woundex.trip_service.domain.value_object.*;

public record TripRequestedEvent(
    TripId tripId,
    RiderId riderId,
    Location pickupLocation,
    Location destination
) {}