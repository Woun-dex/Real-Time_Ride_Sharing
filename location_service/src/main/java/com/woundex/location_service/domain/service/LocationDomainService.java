package com.woundex.location_service.domain.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.woundex.location_service.domain.model.DriverLocation;
import com.woundex.location_service.domain.model.NearbyDriver;
import com.woundex.location_service.domain.model.Position;
import com.woundex.location_service.domain.repository.DriverLocationRepository;
import org.springframework.stereotype.Service;

/**
 * Simple domain service that contains business rules for ingesting and querying locations.
 * Keeps domain logic separate from infra details.
 */
@Service
public class LocationDomainService {

    private final DriverLocationRepository repository;

    public LocationDomainService(DriverLocationRepository repository) {
        this.repository = repository;
    }

    /**
     * Ingest a raw location. Performs minimal domain validation (timestamp sanity).
     */
    public void ingest(UUID driverId, Position pos, Instant timestamp) {
        // simple rule: ignore events that are too far in the future
        if (timestamp.isAfter(Instant.now().plusSeconds(5))) return;
        DriverLocation loc = new DriverLocation(driverId, pos, timestamp);
        repository.upsert(loc);
    }

   
    public List<NearbyDriver> findNearby(Position center, double radiusMeters, int limit) {
        return repository.findNearby(center, radiusMeters, limit);
    }
}