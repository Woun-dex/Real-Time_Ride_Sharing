package com.woundex.ws_driver_service.messaging.kafka;

import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woundex.ws_driver_service.application.handler.TripEventHandler;
import com.woundex.ws_driver_service.domain.event.TripLifecycleEvent;
import com.woundex.ws_driver_service.domain.value_object.DriverId;
import com.woundex.ws_driver_service.domain.value_object.TripId;
import com.woundex.ws_driver_service.messaging.websocket.SessionRegistry;

/**
 * Kafka consumer for trip events (assignments, status updates).
 */
@Component
public class TripEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TripEventConsumer.class);
    private final TripEventHandler tripEventHandler;
    private final ObjectMapper objectMapper;
    private final SessionRegistry sessionRegistry;

    public TripEventConsumer(TripEventHandler tripEventHandler, ObjectMapper objectMapper, SessionRegistry sessionRegistry) {
        this.tripEventHandler = tripEventHandler;
        this.objectMapper = objectMapper;
        this.sessionRegistry = sessionRegistry;
    }

    @KafkaListener(topics = {"trip.started", "trip.cancelled", "driver.completed"})
    public void consume(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            JsonNode node = objectMapper.readTree(message);
            String tripIdValue = node.has("tripId") && node.get("tripId").has("value")
                    ? node.get("tripId").get("value").asText()
                    : node.path("tripId").asText();
            String type = topic; // e.g. "trip.started", "trip.cancelled", "driver.completed"
            TripLifecycleEvent event = new TripLifecycleEvent(
                    TripId.of(tripIdValue), type, Instant.now());
            tripEventHandler.handle(event);
        } catch (Exception e) {
            log.error("Failed to process trip lifecycle event from topic {}: {}", topic, message, e);
        }
    }

    @KafkaListener(topics = "driver.offer")
    public void consumeAssignment(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            String tripId = node.path("tripId").asText();
            String driverId = node.path("driverId").asText();
            String riderId = node.path("riderId").asText(null);
            double pickupLat = node.path("pickupLat").asDouble(0);
            double pickupLon = node.path("pickupLon").asDouble(0);
            double dropoffLat = node.path("dropoffLat").asDouble(0);
            double dropoffLon = node.path("dropoffLon").asDouble(0);
            long expiresAt = node.path("expiresAt").asLong(0);

            log.info("\uD83D\uDCE5 Received driver.offer: tripId={}, driverId={}", tripId, driverId);

            // Build structured JSON matching frontend's EventEnvelope format
            java.util.Map<String, Object> envelope = new java.util.LinkedHashMap<>();
            envelope.put("eventType", "TRIP_REQUESTED");
            envelope.put("tripId", tripId);
            envelope.put("driverId", driverId);
            envelope.put("riderId", riderId);

            java.util.Map<String, Object> pickup = new java.util.LinkedHashMap<>();
            pickup.put("latitude", pickupLat);
            pickup.put("longitude", pickupLon);
            envelope.put("pickup", pickup);

            java.util.Map<String, Object> dropoff = new java.util.LinkedHashMap<>();
            dropoff.put("latitude", dropoffLat);
            dropoff.put("longitude", dropoffLon);
            envelope.put("dropoff", dropoff);

            envelope.put("expiresAt", expiresAt);
            envelope.put("timestamp", java.time.Instant.now().toString());

            // Push to driver's WebSocket session as structured JSON
            DriverId dId = DriverId.of(UUID.fromString(driverId));
            String jsonMessage = objectMapper.writeValueAsString(envelope);
            sessionRegistry.pushRaw(dId, jsonMessage);

            log.info("\u2705 Processed driver.offer for driver {}", driverId);
        } catch (Exception e) {
            log.error("Failed to process driver.offer event: {}", message, e);
        }
    }
}
