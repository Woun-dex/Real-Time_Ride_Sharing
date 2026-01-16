package com.woundex.trip_service.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.woundex.trip_service.application.Commands.TripCommandHandler;
import com.woundex.trip_service.domain.commands.*;
import com.woundex.trip_service.domain.value_object.*;
import com.woundex.trip_service.application.dtos.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripCommandHandler commandHandler;

    @PostMapping
    public ResponseEntity<?> createTrip(@Valid @RequestBody CreateTripRequest request) {
        // DTO → Command (translate external to domain language)
        CreateTripCommand command = new CreateTripCommand(
            new RiderId(request.riderId()),
            new Location(request.pickupLocation().latitude(), request.pickupLocation().longitude()),
            new Location(request.dropoffLocation().latitude(), request.dropoffLocation().longitude())
        );

        TripId tripId = commandHandler.handle(command);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(Map.of("tripId", tripId.value()));
    }

    @PutMapping("/{tripId}/assign-driver")
    public ResponseEntity<?> assignDriver(
        @PathVariable String tripId,
        @Valid @RequestBody AssignDriverRequest request
    ) {
        AssignDriverCommand command = new AssignDriverCommand(
            new TripId(tripId),
            new DriverId(request.driverId())
        );

        commandHandler.handle(command);

        return ResponseEntity.ok(Map.of("message", "Driver assigned successfully"));
    }

    @PutMapping("/{tripId}/start")
    public ResponseEntity<?> startTrip(
        @PathVariable String tripId,
        @RequestParam UUID driverId
    ) {
        StartTripCommand command = new StartTripCommand(
            new TripId(tripId),
            new DriverId(driverId)
        );

        commandHandler.handle(command);

        return ResponseEntity.ok(Map.of("message", "Trip started"));
    }

    @PutMapping("/{tripId}/complete")
    public ResponseEntity<?> completeTrip(@PathVariable String tripId) {
        CompleteTripCommand command = new CompleteTripCommand(
            new TripId(tripId)
        );

        commandHandler.handle(command);

        return ResponseEntity.ok(Map.of("message", "Trip completed"));
    }

    @PutMapping("/{tripId}/cancel")
    public ResponseEntity<?> cancelTrip(
        @PathVariable String tripId
        ) {
        CancelTripCommand command = new CancelTripCommand(
            new TripId(tripId)
        );

        commandHandler.handle(command);

        return ResponseEntity.ok(Map.of("message", "Trip cancelled"));
    }
}