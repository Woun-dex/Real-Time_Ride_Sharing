package com.woundex.ws_rider_service.application.port;

import com.woundex.ws_rider_service.domain.Event.RiderLocationUpdatedEvent;
import com.woundex.ws_rider_service.dto.RiderPushMessage;

public interface PushNotifier {
    void push(RiderPushMessage msg);
    void pushForDriverLocation(RiderLocationUpdatedEvent event);
} 