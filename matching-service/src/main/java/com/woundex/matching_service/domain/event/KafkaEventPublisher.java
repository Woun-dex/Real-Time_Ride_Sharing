package com.woundex.matching_service.domain.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class KafkaEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper mapper;

    public KafkaEventPublisher(KafkaTemplate<String, String> kafka, ObjectMapper mapper) {
        this.kafka = kafka;
        this.mapper = mapper;
    }

    @Override
    public void publish(String topic, String key, Object event) {
        try {
            String json = mapper.writeValueAsString(event);
            kafka.send(topic, key, json)
                 .whenComplete((result, ex) -> {
                     if (ex != null) {
                         log.error("Failed to publish to {} key={}: {}", topic, key, ex.getMessage());
                     } else {
                         log.info("Published to {} key={} offset={}",
                                 topic, key, result.getRecordMetadata().offset());
                     }
                 });
        } catch (Exception e) {
            log.error("Serialization failed for topic {} key={}", topic, key, e);
        }
    }
}