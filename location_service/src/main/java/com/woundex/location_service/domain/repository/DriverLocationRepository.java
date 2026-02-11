package main.java.com.woundex.location_service.domain.repository;

import com.woundex.location_service.domain.model.DriverLocation;
import com.woundex.location_service.domain.model.NearbyDriver;
import com.woundex.location_service.domain.model.Position;

import java.util.List;
import java.util.Optional;
import java.util.UUID;



public interface DriverLocationRepository {
    /**
     * Upsert the latest driver location (idempotent).
     */
    void upsert(DriverLocation location);

    /**
     * Find nearby active drivers around center within radiusMeters, limited by 'limit'.
     * Implementations should respect activity/heartbeat semantics (filter inactive).
     */
    List<NearbyDriver> findNearby(Position center, double radiusMeters, int limit);

    /**
     * Optional lookup by id.
     */
    Optional<DriverLocation> findById(UUID driverId);
}