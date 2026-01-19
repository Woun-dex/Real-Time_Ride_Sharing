package com.woundex.ws_rider_service.messaging.kafka;

import java.util.Objects;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.woundex.ws_rider_service.application.port.EventPublisher;

@Component
public class RiderLocationProducer implements EventPublisher {

    private static final String TOPIC = "rider-locations";

    private final KafkaTemplate<String, Object> kafka;

    public RiderLocationProducer(KafkaTemplate<String, Object> kafka) {
        this.kafka = Objects.requireNonNull(kafka, "kafkaTemplate");
    }

    @Override
    public void publish(Object event) {
        kafka.send(TOPIC, event);
    }

}




