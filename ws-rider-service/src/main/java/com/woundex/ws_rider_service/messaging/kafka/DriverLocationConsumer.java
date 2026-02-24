package com.woundex.ws_rider_service.messaging.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woundex.ws_rider_service.messaging.websocket.SessionRegistry;

@Component
public class DriverLocationConsumer {

    private static final Logger log = LoggerFactory.getLogger(DriverLocationConsumer.class);
    private final SessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;

    public DriverLocationConsumer(SessionRegistry sessionRegistry, ObjectMapper objectMapper) {
        this.sessionRegistry = sessionRegistry;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "driver-locations", containerFactory = "kafkaListenerContainerFactory")
    public void onDriverLocation(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);

            String driverId = node.path("driverId").asText(null);
            double lat = node.path("lat").asDouble(0);
            double lon = node.has("lon") ? node.path("lon").asDouble(0) : node.path("lng").asDouble(0);
            String tripId = node.path("tripId").asText(null);

            if (tripId == null || tripId.isBlank()) {
                log.debug("Skipping driver location without tripId: driverId={}", driverId);
                return;
            }

            // Build structured JSON matching frontend's expected format:
            // { eventType: "LOCATION_UPDATED", payload: { latitude, longitude, driverId } }
            String jsonMessage = objectMapper.writeValueAsString(new java.util.LinkedHashMap<String, Object>() {{
                put("eventType", "LOCATION_UPDATED");
                put("payload", new java.util.LinkedHashMap<String, Object>() {{
                    put("latitude", lat);
                    put("longitude", lon);
                    put("driverId", driverId);
                }});
            }});

            sessionRegistry.pushByTripId(tripId, jsonMessage);
            log.debug("Forwarded driver location for tripId={}, driverId={} ({}, {})", tripId, driverId, lat, lon);
        } catch (Exception e) {
            log.error("Failed to process driver location message: {}", message, e);
        }
    }
}
