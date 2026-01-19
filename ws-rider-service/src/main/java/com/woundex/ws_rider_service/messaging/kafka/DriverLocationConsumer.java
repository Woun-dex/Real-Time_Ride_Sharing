package com.woundex.ws_rider_service.messaging.kafka;

import com.woundex.ws_rider_service.application.port.PushNotifier;
import com.woundex.ws_rider_service.domain.Event.RiderLocationUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DriverLocationConsumer {

    private static final Logger log = LoggerFactory.getLogger(DriverLocationConsumer.class);
    private final PushNotifier pushNotifier;

    public DriverLocationConsumer(PushNotifier pushNotifier) {
        this.pushNotifier = pushNotifier;
    }

    @KafkaListener(topics = "driver-locations", containerFactory = "kafkaListenerContainerFactory")
    public void onDriverLocation(RiderLocationUpdatedEvent event) {
        log.debug("Driver location received: {}", event);
        pushNotifier.pushForDriverLocation(event);
    }
}