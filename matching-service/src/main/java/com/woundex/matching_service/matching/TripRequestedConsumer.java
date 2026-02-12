package com.woundex.matching_service.matching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woundex.matching_service.domain.event.Topics;
import com.woundex.matching_service.domain.event.TripRequestedEvent;

@Component
public class TripRequestedConsumer {

    private static final Logger log = LoggerFactory.getLogger(TripRequestedConsumer.class);

    private final MatchingService matchingService;
    private final ObjectMapper mapper;

    public TripRequestedConsumer(MatchingService matchingService, ObjectMapper mapper) {
        this.matchingService = matchingService;
        this.mapper = mapper;
    }

    @KafkaListener(topics = Topics.TRIP_REQUESTED, groupId = "location-matching")
    public void onTripRequested(String message) {
        try {
            TripRequestedEvent event = mapper.readValue(message, TripRequestedEvent.class);
            log.info("Received trip.requested for trip {}", event.getTripId());
            matchingService.handleTripRequested(event);
        } catch (Exception e) {
            log.error("Failed to process trip.requested: {}", message, e);
        }
    }
}