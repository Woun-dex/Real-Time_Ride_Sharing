package com.woundex.ws_driver_service.application.handler;

import com.woundex.ws_driver_service.application.port.PushNotifier;
import com.woundex.ws_driver_service.domain.event.TripAssignedEvent;
import com.woundex.ws_driver_service.domain.event.TripLifecycleEvent;
import com.woundex.ws_driver_service.dto.DriverPushMessage;

import java.util.Objects;

/**
 * Handles trip events and pushes notifications to drivers.
 */
public class TripEventHandler {

    private final PushNotifier notifier;

    public TripEventHandler(PushNotifier notifier) {
        this.notifier = Objects.requireNonNull(notifier, "notifier must not be null");
    }

    public void handle(TripAssignedEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        notifier.push(DriverPushMessage.from(event));
    }

    public void handle(TripLifecycleEvent event) {
        Objects.requireNonNull(event, "event must not be null");
        // Generic lifecycle event handling - specific types can be dispatched here
    }
}
