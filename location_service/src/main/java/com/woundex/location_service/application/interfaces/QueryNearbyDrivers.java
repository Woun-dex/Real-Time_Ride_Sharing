package com.woundex.location_service.application.interfaces;

import com.woundex.location_service.domain.model.NearbyDriver;

import java.util.List;

public interface QueryNearbyDrivers {
    List<NearbyDriver> findNearby(double lat, double lon, double radiusMeters, int limit);
}