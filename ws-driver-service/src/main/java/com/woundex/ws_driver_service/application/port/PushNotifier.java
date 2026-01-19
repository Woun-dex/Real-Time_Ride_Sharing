package com.woundex.ws_driver_service.application.port;

import com.woundex.ws_driver_service.dto.DriverPushMessage;

/**
 * Port for pushing notifications to drivers via WebSocket.
 */
public interface PushNotifier {
    void push(DriverPushMessage msg);
}
