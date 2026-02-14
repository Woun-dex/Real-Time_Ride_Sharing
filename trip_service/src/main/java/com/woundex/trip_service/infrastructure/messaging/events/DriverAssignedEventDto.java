package com.woundex.trip_service.infrastructure.messaging.events;

public record DriverAssignedEventDto(
    String tripId,
    String driverId,
    String riderId,
    double driverLat,
    double driverLon,
    long timestamp
) {
    
}
