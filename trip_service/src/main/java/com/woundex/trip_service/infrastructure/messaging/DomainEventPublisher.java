package com.woundex.trip_service.infrastructure.messaging;

import java.util.List;

public interface DomainEventPublisher {
    void publish(List<Object> events);
}
