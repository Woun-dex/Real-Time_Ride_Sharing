package com.woundex.matching_service.matching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woundex.matching_service.domain.event.DriverAcceptedEvent;
import com.woundex.matching_service.domain.event.Topics;

@Component
public class DriverAcceptedConsumer {

    private static final Logger log = LoggerFactory.getLogger(DriverAcceptedConsumer.class);

    private final MatchingService matchingService;
    private final ObjectMapper mapper;

    public DriverAcceptedConsumer(MatchingService matchingService, ObjectMapper mapper) {
        this.matchingService = matchingService;
        this.mapper = mapper;
    }

    @KafkaListener(topics = Topics.DRIVER_ACCEPTED, groupId = "location-matching")
    public void onDriverAccepted(String message) {
        try {
            DriverAcceptedEvent event = mapper.readValue(message, DriverAcceptedEvent.class);
            log.info("Received driver.accepted for trip {} from driver {}",
                    event.getTripId(), event.getDriverId());
            matchingService.handleDriverAccepted(event);
        } catch (Exception e) {
            log.error("Failed to process driver.accepted: {}", message, e);
        }
    }
}