package com.woundex.ws_rider_service.messaging.websocket;

import org.springframework.stereotype.Component;

import com.woundex.ws_rider_service.application.port.PushNotifier;
import com.woundex.ws_rider_service.domain.Event.RiderLocationUpdatedEvent;
import com.woundex.ws_rider_service.domain.value_object.RiderId;
import com.woundex.ws_rider_service.dto.RiderPushMessage;

@Component
public class WebSocketPushNotifier implements PushNotifier {

    private final SessionRegistry sessionRegistry;

    public WebSocketPushNotifier(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void push(RiderPushMessage msg) {
        RiderId riderId = RiderId.of(msg.getRiderId());
        sessionRegistry.push(riderId, msg);
    }

    @Override
    public void pushForDriverLocation(RiderLocationUpdatedEvent event) {
        RiderId riderId = event.riderId();
        RiderPushMessage msg = new RiderPushMessage(
                riderId.toString(),
                "Driver location update: " + event.location().toString()
        );
        sessionRegistry.push(riderId, msg);
    }
}
