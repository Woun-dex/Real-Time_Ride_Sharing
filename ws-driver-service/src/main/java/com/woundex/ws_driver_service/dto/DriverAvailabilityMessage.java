package com.woundex.ws_driver_service.dto;

import com.woundex.ws_driver_service.domain.value_object.DriverAvailability;

/**
 * DTO for driver availability state change requests from the client.
 */
public class DriverAvailabilityMessage {
    private String driverId;
    private DriverAvailability availability;

    public DriverAvailabilityMessage() {}

    public DriverAvailabilityMessage(String driverId, DriverAvailability availability) {
        this.driverId = driverId;
        this.availability = availability;
    }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    public DriverAvailability getAvailability() { return availability; }
    public void setAvailability(DriverAvailability availability) { this.availability = availability; }
}
