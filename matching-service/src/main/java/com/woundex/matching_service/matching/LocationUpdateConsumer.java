package com.woundex.matching_service.matching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woundex.matching_service.domain.event.DriverLocationEvent;
import com.woundex.matching_service.domain.event.Topics;
import com.woundex.matching_service.infra.redis.RedisGeoService;

@Component
public class LocationUpdateConsumer {

    private static final Logger log = LoggerFactory.getLogger(LocationUpdateConsumer.class);

    private final RedisGeoService geo;
    private final ObjectMapper mapper;

    public LocationUpdateConsumer(RedisGeoService geo, ObjectMapper mapper) {
        this.geo = geo;
        this.mapper = mapper;
    }

    @KafkaListener(topics = Topics.DRIVER_LOCATION, groupId = "location-service")
    public void onDriverLocation(String message) {
        try {
            DriverLocationEvent event = mapper.readValue(message, DriverLocationEvent.class);
            geo.updateDriverLocation(event.getDriverId(), event.getLat(), event.getLon());
        } catch (Exception e) {
            log.error("Failed to process driver.location: {}", message, e);
        }
    }
}