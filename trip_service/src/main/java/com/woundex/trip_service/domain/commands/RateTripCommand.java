package com.woundex.trip_service.domain.commands;

import com.woundex.trip_service.domain.value_object.TripId;

public record RateTripCommand(
    TripId tripId,
    int rating
) {}
