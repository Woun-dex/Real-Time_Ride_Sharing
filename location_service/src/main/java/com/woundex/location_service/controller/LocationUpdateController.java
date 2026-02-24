package com.woundex.location_service.controller;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woundex.location_service.domain.model.DriverLocation;
import com.woundex.location_service.infra.redis.RedisGeoService;

/**
 * REST endpoint for driver GPS location updates.
 * Publishes to Kafka "driver-locations" topic so both
 * location_service and matching-service can consume it.
 */
@RestController
@RequestMapping("/api/location")
public class LocationUpdateController {

    private static final Logger log = LoggerFactory.getLogger(LocationUpdateController.class);
    private static final String TOPIC = "driver-locations";

    private final KafkaTemplate<String, Object> kafka;
    private final RedisGeoService geoService;

    public LocationUpdateController(KafkaTemplate<String, Object> kafka, RedisGeoService geoService) {
        this.kafka = kafka;
        this.geoService = geoService;
    }

    // ──────────────────────────────────────────────────────────
    //  GET /api/location/driver/{driverId}  –  Get driver location
    // ──────────────────────────────────────────────────────────
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<?> getDriverLocation(@PathVariable UUID driverId) {
        // First try with heartbeat check
        Optional<DriverLocation> loc = geoService.findById(driverId);
        if (loc.isPresent()) {
            DriverLocation dl = loc.get();
            return ResponseEntity.ok(Map.of(
                "driverId", dl.getDriverId().toString(),
                "lat", dl.getPosition().getLat(),
                "lon", dl.getPosition().getLon(),
                "timestamp", dl.getTimestamp().toEpochMilli(),
                "active", true
            ));
        }
        // Fallback: return geo position even if heartbeat expired
        Optional<DriverLocation> stale = geoService.findPositionOnly(driverId);
        if (stale.isPresent()) {
            DriverLocation dl = stale.get();
            return ResponseEntity.ok(Map.of(
                "driverId", dl.getDriverId().toString(),
                "lat", dl.getPosition().getLat(),
                "lon", dl.getPosition().getLon(),
                "timestamp", dl.getTimestamp().toEpochMilli(),
                "active", false
            ));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateLocation(@RequestBody Map<String, Object> body) {
        try {
            String driverId = (String) body.get("driverId");
            if (driverId == null || driverId.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "driverId is required"));
            }

            double lat = toDouble(body.get("lat"), body.get("latitude"));
            // Accept "lng", "lon", or "longitude"
            double lon = toDouble(body.get("lng"), body.get("lon"), body.get("longitude"));

            long ts = Instant.now().toEpochMilli();
            if (body.containsKey("timestamp")) {
                Object tsObj = body.get("timestamp");
                if (tsObj instanceof Number) {
                    ts = ((Number) tsObj).longValue();
                } else if (tsObj instanceof String) {
                    try {
                        ts = Instant.parse((String) tsObj).toEpochMilli();
                    } catch (Exception e) {
                        log.warn("Failed to parse timestamp string: {}", tsObj);
                    }
                }
            }

            // Forward tripId if provided (enables ws-rider-service to route updates)
            String tripId = body.get("tripId") != null ? body.get("tripId").toString() : null;

            // Build a Map and let JsonSerializer handle serialization
            Map<String, Object> event = new LinkedHashMap<>();
            event.put("driverId", driverId);
            event.put("lat", lat);
            event.put("lon", lon);
            event.put("timestamp", ts);
            if (tripId != null && !tripId.isBlank()) {
                event.put("tripId", tripId);
            }

            kafka.send(TOPIC, driverId, event);
            log.info("📍 Published location update for driver {} ({}, {})", driverId, lat, lon);

            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (Exception e) {
            log.error("Failed to process location update: {}", body, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to process location update"));
        }
    }

    private double toDouble(Object... candidates) {
        for (Object c : candidates) {
            if (c instanceof Number) return ((Number) c).doubleValue();
        }
        return 0.0;
    }
}
