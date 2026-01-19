package com.woundex.ws_driver_service.application.port;

/**
 * Port for publishing events to the messaging infrastructure (Kafka).
 */
public interface EventPublisher {
    void publish(Object event);
}
