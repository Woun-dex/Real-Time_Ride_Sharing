package com.woundex.trip_service.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaDomainEventPublisher implements DomainEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(List<Object> events) {
        events.forEach(this::publishSingle);
    }

    private void publishSingle(Object event) {
        try {
            String topic = getTopicName(event);
            String eventJson = objectMapper.writeValueAsString(event);
            
            kafkaTemplate.send(topic, eventJson)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event: {}", event.getClass().getSimpleName(), ex);
                    } else {
                        log.info("Published event: {} to topic: {}", 
                            event.getClass().getSimpleName(), topic);
                    }
                });
        } catch (Exception e) {
            log.error("Error serializing event: {}", event.getClass().getSimpleName(), e);
        }
    }

    private String getTopicName(Object event) {
        String simpleName = event.getClass().getSimpleName();
        return simpleName
            .replaceAll("Event$", "")
            .replaceAll("([a-z])([A-Z])", "$1.$2")
            .toLowerCase();
    }
}