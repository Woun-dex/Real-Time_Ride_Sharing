package com.woundex.ws_rider_service.messaging.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.woundex.ws_rider_service.application.handler.TripEventHandler;
import com.woundex.ws_rider_service.domain.Event.TripLifecycleEvent;

@Component
public class TripEventConsumer {

    private final TripEventHandler tripEventHandler;

    public TripEventConsumer(TripEventHandler tripEventHandler) {
        this.tripEventHandler = tripEventHandler;
    }

    @KafkaListener(topics = "trip-events")
    public void consume(TripLifecycleEvent event) {
        tripEventHandler.handle(event);
    }
    
}
