package com.woundex.ws_rider_service.messaging.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.woundex.ws_rider_service.application.handler.TripEventHandler;
import com.woundex.ws_rider_service.domain.Event.TripLifecycleEvent;
import com.woundex.ws_rider_service.domain.value_object.TripId;

import java.time.Instant;

@Component
public class TripEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TripEventConsumer.class);
    private final TripEventHandler tripEventHandler;
    private final ObjectMapper objectMapper;

    public TripEventConsumer(TripEventHandler tripEventHandler, ObjectMapper objectMapper) {
        this.tripEventHandler = tripEventHandler;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = {"trip.started", "trip.cancelled", "driver.completed"})
    public void consume(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        try {
            JsonNode node = objectMapper.readTree(message);
            String tripIdValue = node.has("tripId") && node.get("tripId").has("value")
                    ? node.get("tripId").get("value").asText()
                    : node.path("tripId").asText();
            TripLifecycleEvent event = new TripLifecycleEvent(
                    TripId.of(tripIdValue), topic, Instant.now());
            tripEventHandler.handle(event);
        } catch (Exception e) {
            log.error("Failed to process trip lifecycle event from topic {}: {}", topic, message, e);
        }
    }
    
}
