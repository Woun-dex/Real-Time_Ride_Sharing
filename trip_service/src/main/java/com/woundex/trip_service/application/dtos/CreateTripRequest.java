package com.woundex.trip_service.application.dtos;


import java.util.UUID;

import com.woundex.trip_service.domain.value_object.RiderId;

import jakarta.validation.constraints.NotNull;

public record CreateTripRequest(
    @NotNull UUID riderId,
    @NotNull LocationDTO pickupLocation,
    @NotNull LocationDTO dropoffLocation
) {
} 