package com.woundex.matching_service.infra.redis;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.woundex.matching_service.domain.model.NearbyDriver;
import com.woundex.matching_service.domain.model.Position;

@Service
public class RedisGeoService {

    private static final String GEO_KEY = "drivers:locations";

    private final GeoOperations<String, String> geoOps;

    public RedisGeoService(StringRedisTemplate redis) {
        this.geoOps = redis.opsForGeo();
    }

    /** Store or update a driver's position. */
    public void updateDriverLocation(String driverId, double lat, double lon) {
        geoOps.add(GEO_KEY, new Point(lon, lat), driverId);
    }

    /** Remove a driver (e.g. went offline). */
    public void removeDriver(String driverId) {
        geoOps.remove(GEO_KEY, driverId);
    }

    /** Find nearby drivers sorted by distance (closest first). */
    public List<NearbyDriver> findNearby(Position center, double radiusMeters, int limit) {
        Circle within = new Circle(
                new Point(center.getLon(), center.getLat()),
                new Distance(radiusMeters, Metrics.MILES));

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs()
                .includeCoordinates()
                .includeDistance()
                .sortAscending()
                .limit(limit);

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = geoOps.radius(GEO_KEY, within, args);

        List<NearbyDriver> drivers = new ArrayList<>();
        if (results == null) return drivers;

        for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {
            RedisGeoCommands.GeoLocation<String> loc = result.getContent();
            Point point = loc.getPoint();

            drivers.add(new NearbyDriver(
                    loc.getName(),
                    new Position(point.getY(), point.getX()),   // y=lat, x=lon
                    result.getDistance().getValue()));
        }
        return drivers;
    }
}