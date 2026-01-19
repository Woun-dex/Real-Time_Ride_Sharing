package com.woundex.ws_driver_service.messaging.kafka;

import com.woundex.ws_driver_service.application.port.EventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Component
public class DriverAvailabilityProducer implements EventPublisher {

    private static final String TOPIC = "driver-availability";

    private final KafkaTemplate<String, Object> kafka;

    public DriverAvailabilityProducer(KafkaTemplate<String, Object> kafka) {
        this.kafka = Objects.requireNonNull(kafka, "kafkaTemplate must not be null");
    }

    @Override
    public void publish(Object event) {
        kafka.send(TOPIC, event);
    }
}
