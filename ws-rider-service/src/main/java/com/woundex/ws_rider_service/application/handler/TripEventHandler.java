package com.woundex.ws_rider_service.application.handler;

import java.util.Objects;

import com.woundex.ws_rider_service.application.port.PushNotifier;
import com.woundex.ws_rider_service.domain.Event.TripAssignedEvent;
import com.woundex.ws_rider_service.domain.Event.TripLifecycleEvent;
import com.woundex.ws_rider_service.dto.RiderPushMessage;

public class TripEventHandler {

    private final PushNotifier notifier;

    public TripEventHandler(PushNotifier notifier) {
        this.notifier = Objects.requireNonNull(notifier , "notifier must not be null");
    }

    public void handle(TripAssignedEvent event){
        Objects.requireNonNull(event , "event must not be null");

            notifier.push(RiderPushMessage.from(event));
        
    }

    // Overload to accept generic lifecycle events (delegation or filtering can be added later)
    public void handle(TripLifecycleEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        // Intentionally left minimal; concrete handling is performed for specific subclasses like TripAssignedEvent
    }
    
}
