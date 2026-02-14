package com.woundex.trip_service.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woundex.trip_service.domain.commands.AssignDriverCommand;
import com.woundex.trip_service.domain.value_object.DriverId;
import com.woundex.trip_service.domain.value_object.TripId;
import com.woundex.trip_service.application.Commands.TripCommandHandler;
import com.woundex.trip_service.infrastructure.messaging.events.DriverAssignedEventDto;
import com.woundex.trip_service.infrastructure.cache.RedisCacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TripEventConsumer {

    private final ObjectMapper objectMapper;
    private final TripCommandHandler commandHandler;
    private final RedisCacheService redisCacheService;

    @KafkaListener(topics = "driver.assigned", groupId = "trip-service")
    public void onDriverAssigned(String message) {
        try {
            log.info("Received driver.assigned event: {}", message);
            
            // 1. Deserialize the Kafka message
            DriverAssignedEventDto event = objectMapper.readValue(
                message, 
                DriverAssignedEventDto.class
            );
            
            // 2. Create domain command
            AssignDriverCommand command = new AssignDriverCommand(
                new TripId(event.tripId()),
                DriverId.of(java.util.UUID.fromString(event.driverId()))
            );
            
            // 3. Execute the command (updates trip status to ASSIGNED)
            commandHandler.handle(command);
            
            // 4. Cache driver info in Redis for fast lookups
            cacheDriverInfo(event);
            
            log.info("Successfully assigned driver {} to trip {}", 
                event.driverId(), event.tripId());
            
        } catch (Exception e) {
            log.error("Failed to process driver.assigned event: {}", message, e);
        }
    } 
    

    @KafkaListener(topics = "rider-locations", groupId = "trip-service")
    public void onRiderLocationUpdate(String message) {
        log.info("Received rider location update: {}", message);
        // Update Redis with rider location
    }

    private void cacheDriverInfo(DriverAssignedEventDto event) {
        String key = "trip:driver:" + event.tripId();
        redisCacheService.cacheDriverInfo(key, event);
    }
}