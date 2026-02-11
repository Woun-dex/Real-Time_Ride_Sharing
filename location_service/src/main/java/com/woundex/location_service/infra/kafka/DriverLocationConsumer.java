package com.woundex.location_service.infra.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woundex.location_service.application.dto.DriverLocationDto;
import com.woundex.location_service.application.interfaces.IngestDriverLocation;

@Component
public class DriverLocationConsumer {

    private static final Logger log = LoggerFactory.getLogger(DriverLocationConsumer.class);
    private final ObjectMapper mapper;
    private final IngestDriverLocation ingest;

    public DriverLocationConsumer(ObjectMapper mapper, IngestDriverLocation ingest) {
        this.mapper = mapper;
        this.ingest = ingest;
    }

    @KafkaListener(topics = "driver-locations", containerFactory = "kafkaListenerContainerFactory")
    public void onMessage(String payload) {
        try {
            DriverLocationDto dto = mapper.readValue(payload, DriverLocationDto.class);
            if (dto == null || dto.getDriverId() == null) {
                log.warn("dropping invalid driver-location payload: {}", payload);
                return;
            }
            ingest.ingest(dto);
        } catch (Exception e) {
            log.error("failed to handle driver-locations message", e);
        }
    }
}