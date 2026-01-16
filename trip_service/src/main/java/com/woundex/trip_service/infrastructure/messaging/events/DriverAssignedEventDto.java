package com.woundex.trip_service.infrastructure.messaging.events;

import java.util.UUID;

public record DriverAssignedEventDto(
    String tripId,
    UUID driverId,
    String driverName,
    String driverPhoneNumber,
    String vehicleInfo,
    Integer estimatedArrivalMinutes
) {
    
}
