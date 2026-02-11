package com.woundex.location_service.application.interfaces;

import java.util.List;

import com.woundex.location_service.domain.model.NearbyDriver;
import com.woundex.location_service.domain.model.Position;

public interface QueryNearbyDrivers {
    List<NearbyDriver> findNearby(Position center, double radiusMeters, int limit);
}