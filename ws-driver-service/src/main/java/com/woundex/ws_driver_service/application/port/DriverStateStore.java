package com.woundex.ws_driver_service.application.port;

import com.woundex.ws_driver_service.domain.value_object.DriverAvailability;
import com.woundex.ws_driver_service.domain.value_object.DriverId;

/**
 * Port for managing driver availability state.
 */
public interface DriverStateStore {
    DriverAvailability getAvailability(DriverId driverId);
    void setAvailability(DriverId driverId, DriverAvailability availability);
}
