package com.woundex.trip_service.application.Commands;

import org.springframework.stereotype.Service;

import com.woundex.trip_service.domain.commands.AssignDriverCommand;
import com.woundex.trip_service.domain.commands.CancelTripCommand;
import com.woundex.trip_service.domain.commands.CompleteTripCommand;
import com.woundex.trip_service.domain.commands.CreateTripCommand;
import com.woundex.trip_service.domain.commands.StartTripCommand;
import com.woundex.trip_service.domain.entities.Trip_Entity;
import com.woundex.trip_service.domain.value_object.TripId;
import com.woundex.trip_service.infrastructure.messaging.DomainEventPublisher;
import com.woundex.trip_service.infrastructure.persistence.TripRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TripCommandHandler {

    private final TripRepository tripRepository;
    private final DomainEventPublisher domainEventPublisher;

   @Transactional
    public TripId handle(CreateTripCommand cmd) {
        TripId tripId = TripId.generate();
        Trip_Entity trip = Trip_Entity.create(
            tripId, 
            cmd.riderId(), 
            cmd.pickupLocation(), 
            cmd.dropoffLocation()
        );
        
        tripRepository.save(trip);
        domainEventPublisher.publish(trip.getDomainEvents());
        trip.clearEvents();
        
        return tripId;
    }

    @Transactional
    public void handle(AssignDriverCommand cmd) {
        Trip_Entity trip = tripRepository.findById(cmd.tripId().value())
            .orElseThrow(() -> new IllegalArgumentException("Trip not found"));
    
        trip.assignDriver(cmd.driverId());
        
        tripRepository.save(trip);
        domainEventPublisher.publish(trip.getDomainEvents());
        trip.clearEvents();
    }

     @Transactional
    public void handle(StartTripCommand cmd) {
        Trip_Entity trip = tripRepository.findById(cmd.tripId().value())
            .orElseThrow(() -> new IllegalArgumentException("Trip not found"));
        
        if (!trip.getDriverId().map(d -> d.equals(cmd.driverId())).orElse(false)) {
            throw new IllegalStateException("Driver mismatch");
        }
        
        trip.start();
        
        tripRepository.save(trip);
        domainEventPublisher.publish(trip.getDomainEvents());
        trip.clearEvents();
    }

    @Transactional
    public void handle(CompleteTripCommand cmd) {
        Trip_Entity trip = tripRepository.findById(cmd.tripId().value())
            .orElseThrow(() -> new IllegalArgumentException("Trip not found"));
        
        trip.complete();
        
        tripRepository.save(trip);
        domainEventPublisher.publish(trip.getDomainEvents());
        trip.clearEvents();
    }

     @Transactional
    public void handle(CancelTripCommand cmd) {
        Trip_Entity trip = tripRepository.findById(cmd.tripId().value())
            .orElseThrow(() -> new IllegalArgumentException("Trip not found"));
        
        trip.cancel();
        
        tripRepository.save(trip);
        domainEventPublisher.publish(trip.getDomainEvents());
        trip.clearEvents();
    }

    
}
