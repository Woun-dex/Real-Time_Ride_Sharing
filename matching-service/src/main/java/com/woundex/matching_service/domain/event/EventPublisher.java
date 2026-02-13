package com.woundex.matching_service.domain.event;

public interface EventPublisher {
    void publish(String topic, String key, Object event);
}