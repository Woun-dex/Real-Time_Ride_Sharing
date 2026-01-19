package com.woundex.ws_driver_service.infrastructure;

import com.woundex.ws_driver_service.application.port.PushNotifier;
import com.woundex.ws_driver_service.domain.value_object.DriverId;
import com.woundex.ws_driver_service.dto.DriverPushMessage;
import com.woundex.ws_driver_service.messaging.websocket.SessionRegistry;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * WebSocket-based implementation of PushNotifier.
 */
@Component
public class WebSocketPushNotifier implements PushNotifier {

    private final SessionRegistry sessionRegistry;

    public WebSocketPushNotifier(SessionRegistry sessionRegistry) {
        this.sessionRegistry = Objects.requireNonNull(sessionRegistry, "sessionRegistry must not be null");
    }

    @Override
    public void push(DriverPushMessage msg) {
        Objects.requireNonNull(msg, "msg must not be null");
        DriverId driverId = DriverId.of(msg.getDriverId());
        sessionRegistry.push(driverId, msg);
    }
}
