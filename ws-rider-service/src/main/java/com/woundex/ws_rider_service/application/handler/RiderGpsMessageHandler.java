
package com.woundex.ws_rider_service.application.handler;

import java.time.Instant;
import java.util.Objects;

import com.woundex.ws_rider_service.domain.value_object.Location;

import com.woundex.ws_rider_service.dto.RiderGpsMessage;

import com.woundex.ws_rider_service.domain.value_object.RiderId;

import com.woundex.ws_rider_service.domain.Event.RiderLocationUpdatedEvent;

import com.woundex.ws_rider_service.application.port.EventPublisher;

public class RiderGpsMessageHandler {

    private final EventPublisher publisher;

    public RiderGpsMessageHandler(EventPublisher publisher) {
        this.publisher = Objects.requireNonNull(publisher , "publisher must not be null");
    }

    public void handle(RiderGpsMessage msg){
        Objects.requireNonNull(msg , "msg must not be null");

        RiderLocationUpdatedEvent event = new RiderLocationUpdatedEvent(
                RiderId.of(msg.getRiderId()),
                new Location(msg.getLat(), msg.getLng()),
                Instant.now()
        );

        publisher.publish(event);
    }
    
}