package com.woundex.ws_rider_service.application.port;

public interface EventPublisher {
    void publish(Object event);
}