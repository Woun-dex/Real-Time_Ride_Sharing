package com.woundex.ws_rider_service.messaging.kafka;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woundex.ws_rider_service.application.port.PushNotifier;
import com.woundex.ws_rider_service.domain.Event.RiderLocationUpdatedEvent;
import com.woundex.ws_rider_service.domain.value_object.Location;
import com.woundex.ws_rider_service.domain.value_object.RiderId;

@Component
public class DriverLocationConsumer {

    private static final Logger log = LoggerFactory.getLogger(DriverLocationConsumer.class);
    private final PushNotifier pushNotifier;
    private final ObjectMapper objectMapper;

    public DriverLocationConsumer(PushNotifier pushNotifier, ObjectMapper objectMapper) {
        this.pushNotifier = pushNotifier;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "driver-locations", containerFactory = "kafkaListenerContainerFactory")
    public void onDriverLocation(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            String riderIdValue = node.has("riderId") && node.get("riderId").has("value")
                    ? node.get("riderId").get("value").asText()
                    : node.path("riderId").asText();
            JsonNode locNode = node.path("location");
            double lat = locNode.path("lat").asDouble();
            double lng = locNode.path("lng").asDouble();
            log.debug("Driver location received: riderId={}, lat={}, lng={}", riderIdValue, lat, lng);
            RiderLocationUpdatedEvent evt = new RiderLocationUpdatedEvent(
                    RiderId.of(riderIdValue), new Location(lat, lng), Instant.now());
            pushNotifier.pushForDriverLocation(evt);
        } catch (Exception e) {
            log.error("Failed to process driver location message: {}", message, e);
        }
    }
}