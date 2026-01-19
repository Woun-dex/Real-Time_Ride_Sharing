package com.woundex.ws_driver_service.infrastructure;

import com.woundex.ws_driver_service.application.port.DriverStateStore;
import com.woundex.ws_driver_service.domain.value_object.DriverAvailability;
import com.woundex.ws_driver_service.domain.value_object.DriverId;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of driver state store.
 * In production, this would be backed by Redis or similar.
 */
@Component
public class InMemoryDriverStateStore implements DriverStateStore {

    private final Map<DriverId, DriverAvailability> states = new ConcurrentHashMap<>();

    @Override
    public DriverAvailability getAvailability(DriverId driverId) {
        return states.getOrDefault(driverId, DriverAvailability.OFFLINE);
    }

    @Override
    public void setAvailability(DriverId driverId, DriverAvailability availability) {
        states.put(driverId, availability);
    }
}
