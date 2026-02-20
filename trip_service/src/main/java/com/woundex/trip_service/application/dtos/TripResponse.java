package com.woundex.trip_service.application.dtos;

import java.util.UUID;

public record TripResponse(
    UUID tripId,
    UUID riderId,
    UUID driverId,
    LocationDTO pickup,
    LocationDTO dropoff,
    String status,
    Integer rating
) {
    
}
