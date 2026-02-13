package com.woundex.gateway_service.flow.model;

import java.time.Instant;

/**
 * Canonical event envelope that flows through Kafka topics.
 * <p>
 * Every service that publishes or consumes trip-related events
 * serialises / deserialises this record.
 *
 * <h3>Example JSON</h3>
 * <pre>
 * {
 *   "eventId":   "evt-abc-123",
 *   "eventType": "TRIP_REQUESTED",
 *   "tripId":    "trip-001",
 *   "riderId":   "rider-001",
 *   "driverId":  null,
 *   "pickup":    { "latitude": 40.7128, "longitude": -74.006, "address": "NYC" },
 *   "dropoff":   { "latitude": 40.758,  "longitude": -73.985, "address": "Times Sq" },
 *   "fare":      null,
 *   "status":    "REQUESTED",
 *   "timestamp": "2026-02-13T10:15:30Z"
 * }
 * </pre>
 *
 * @see com.woundex.gateway_service.flow.TripMatchingFlowTest
 * @see com.woundex.gateway_service.flow.TripStatusLifecycleFlowTest
 */
public record TripEvent(
    String  eventId,
    String  eventType,   // REQUESTED, MATCHED, ACCEPTED, DRIVER_EN_ROUTE, ARRIVED, IN_PROGRESS, COMPLETED …
    String  tripId,
    String  riderId,
    String  driverId,
    Location pickup,
    Location dropoff,
    Double  fare,
    String  status,
    Instant timestamp
) {

    /**
     * Geo-coordinate with optional human-readable address.
     */
    public record Location(double latitude, double longitude, String address) {}
}
