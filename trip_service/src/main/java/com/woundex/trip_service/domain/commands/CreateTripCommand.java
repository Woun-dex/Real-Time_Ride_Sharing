package com.woundex.trip_service.domain.commands;

import com.woundex.trip_service.domain.value_object.*;

public record CreateTripCommand(
    RiderId riderId,
    Location pickupLocation,
    Location dropoffLocation
) {
    public CreateTripCommand {
        if (riderId == null) {
            throw new IllegalArgumentException("RiderId Required");
        }
        if (pickupLocation == null) {
            throw new IllegalArgumentException("PickupLocation Required");
        }
        if (dropoffLocation == null) {
            throw new IllegalArgumentException("DropoffLocation Required");
        }
    }
  
}
