package com.woundex.trip_service.application.dtos;

import java.util.UUID;

import com.woundex.trip_service.domain.value_object.*;

public record TripResponse(
    UUID tripId,
    UUID riderId,
    UUID driverId,
    LocationDTO pickup,
    LocationDTO dropoff,
    String status
) {
    
}
