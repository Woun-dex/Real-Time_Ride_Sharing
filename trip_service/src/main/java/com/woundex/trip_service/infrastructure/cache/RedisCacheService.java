package com.woundex.trip_service.infrastructure.cache;

import com.woundex.trip_service.domain.value_object.Location;
import com.woundex.trip_service.domain.value_object.RiderId;
import com.woundex.trip_service.infrastructure.messaging.events.DriverAssignedEventDto;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration LOCATION_TTL = Duration.ofMinutes(10);
    private static final Duration DRIVER_INFO_TTL = Duration.ofHours(2);


    public void cacheRiderLocation(RiderId riderId, Location location) {
        String key = "rider:location:" + riderId.value();
        redisTemplate.opsForValue().set(key, location, LOCATION_TTL);
    }

    public Optional<Location> getRiderLocation(RiderId riderId) {
        String key = "rider:location:" + riderId.value();
        Object value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable((Location) value);
    }

    public void cacheActiveTripId(RiderId riderId, String tripId) {
        String key = "rider:active-trip:" + riderId.value();
        redisTemplate.opsForValue().set(key, tripId, Duration.ofHours(2));
    }

    public Optional<String> getActiveTripId(RiderId riderId) {
        String key = "rider:active-trip:" + riderId.value();
        Object value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable((String) value);
    }

    public void cacheDriverInfo(String key, DriverAssignedEventDto driverInfo) {
        redisTemplate.opsForValue().set(key, driverInfo, DRIVER_INFO_TTL);
    }
    
    public Optional<DriverAssignedEventDto> getDriverInfo(String tripId) {
        String key = "trip:driver:" + tripId;
        Object value = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable((DriverAssignedEventDto) value);
    }
    
    public void clearDriverInfo(String tripId) {
        String key = "trip:driver:" + tripId;
        redisTemplate.delete(key);
    }
}