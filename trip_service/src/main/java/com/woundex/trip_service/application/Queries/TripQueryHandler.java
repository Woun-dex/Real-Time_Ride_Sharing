package com.woundex.trip_service.application.Queries;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.woundex.trip_service.application.dtos.LocationDTO;
import com.woundex.trip_service.application.dtos.TripResponse;
import com.woundex.trip_service.domain.entities.Trip_Entity;
import com.woundex.trip_service.infrastructure.persistence.TripRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripQueryHandler {

    private final TripRepository tripRepository;

    public TripResponse findById(UUID tripId) {
        Trip_Entity trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new IllegalArgumentException("Trip not found: " + tripId));
        return toResponse(trip);
    }

    public List<TripResponse> findHistoryByRider(UUID riderId) {
        return tripRepository.findByRiderId(riderId).stream()
            .map(this::toResponse)
            .toList();
    }

    public List<TripResponse> findHistoryByDriver(UUID driverId) {
        return tripRepository.findByDriverId(driverId).stream()
            .map(this::toResponse)
            .toList();
    }

    private TripResponse toResponse(Trip_Entity trip) {
        return new TripResponse(
            trip.getId().value(),
            trip.getRiderId().value(),
            trip.getDriverId().map(d -> d.value()).orElse(null),
            new LocationDTO(trip.getPickup().lat(), trip.getPickup().lng()),
            new LocationDTO(trip.getDestination().lat(), trip.getDestination().lng()),
            trip.getStatus().name(),
            trip.getRating().orElse(null)
        );
    }
}
