package com.woundex.location_service.infra.redis;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoRadiusCommandArgs;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.woundex.location_service.application.interfaces.QueryNearbyDrivers;
import com.woundex.location_service.domain.model.DriverLocation;
import com.woundex.location_service.domain.model.NearbyDriver;
import com.woundex.location_service.domain.model.Position;
import com.woundex.location_service.domain.repository.DriverLocationRepository;

/**
 * Redis adapter implementing domain repository and query port.
 * - GEO key: drivers:geo
 * - Heartbeat key: driver:{id}:heartbeat (value = epoch millis) TTL 15s
 */
@Component
public class RedisGeoService implements DriverLocationRepository, QueryNearbyDrivers {

    private final StringRedisTemplate redis;
    private static final String GEO_KEY = "drivers:geo";
    private static final String HEARTBEAT_FMT = "driver:%s:heartbeat";
    private static final Duration HEARTBEAT_TTL = Duration.ofSeconds(15);

    public RedisGeoService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void upsert(DriverLocation location) {
        String id = location.getDriverId().toString();
        double lat = location.getPosition().getLat();
        double lon = location.getPosition().getLon();
        // GEOADD expects (lon, lat)
        redis.opsForGeo().add(GEO_KEY, new Point(lon, lat), id);
        String hb = String.format(HEARTBEAT_FMT, id);
        // store epoch millis if available, else now
        String ts = String.valueOf(location.getTimestamp() != null ? location.getTimestamp().toEpochMilli() : Instant.now().toEpochMilli());
        redis.opsForValue().set(hb, ts, HEARTBEAT_TTL);
    }

    @Override
    public List<NearbyDriver> findNearby(Position center, double radiusMeters, int limit) {
        double km = radiusMeters / 1000.0;
        Circle area = new Circle(new Point(center.getLon(), center.getLat()), new Distance(km, Metrics.KILOMETERS));
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redis.opsForGeo().radius(GEO_KEY, area,
                GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().sortAscending().limit(limit));
        if (results == null) return List.of();
        List<NearbyDriver> out = new ArrayList<>();
        for (GeoResult<RedisGeoCommands.GeoLocation<String>> r : results) {
            String member = r.getContent().getName();
            String hb = String.format(HEARTBEAT_FMT, member);
            if (!Boolean.TRUE.equals(redis.hasKey(hb))) continue; // filter inactive
            Double distKm = r.getDistance() != null ? r.getDistance().getValue() : null;
            double distMeters = distKm == null ? 0.0 : distKm * 1000.0;
            out.add(new NearbyDriver(UUID.fromString(member), distMeters));
        }
        return out.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public Optional<DriverLocation> findById(UUID driverId) {
        String id = driverId.toString();
        List<Point> pos = redis.opsForGeo().position(GEO_KEY, id);
        if (pos == null || pos.isEmpty() || pos.get(0) == null) return Optional.empty();
        Point p = pos.get(0);
        String hbKey = String.format(HEARTBEAT_FMT, id);
        String tsStr = redis.opsForValue().get(hbKey);
        if (tsStr == null) return Optional.empty(); // treat missing heartbeat as inactive
        long epoch;
        try { epoch = Long.parseLong(tsStr); } catch (Exception e) { epoch = Instant.now().toEpochMilli(); }
        DriverLocation dl = new DriverLocation(driverId, new Position(p.getY(), p.getX()), Instant.ofEpochMilli(epoch));
        return Optional.of(dl);
    }

    /**
     * Remove GEO members that have no heartbeat key.
     * Runs within a single RedisCallback to minimize roundtrips.
     */
    public void cleanupExpiredMembers() {
        byte[] geoKeyBytes = GEO_KEY.getBytes(StandardCharsets.UTF_8);
        redis.execute((RedisCallback<Void>) connection -> {
            Set<byte[]> members = connection.zRange(geoKeyBytes, 0, -1);
            if (members == null || members.isEmpty()) return null;
            for (byte[] mem : members) {
                String member = new String(mem, StandardCharsets.UTF_8);
                String hb = String.format(HEARTBEAT_FMT, member);
                Boolean exists = connection.exists(hb.getBytes(StandardCharsets.UTF_8));
                if (exists == null || !exists) {
                    connection.zRem(geoKeyBytes, mem);
                }
            }
            return null;
        });
    }
}