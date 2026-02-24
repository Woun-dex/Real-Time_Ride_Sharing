package com.woundex.trip_service.api.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woundex.trip_service.application.Commands.TripCommandHandler;
import com.woundex.trip_service.application.Queries.TripQueryHandler;
import com.woundex.trip_service.application.dtos.AssignDriverRequest;
import com.woundex.trip_service.application.dtos.CreateTripRequest;
import com.woundex.trip_service.application.dtos.RateTripRequest;
import com.woundex.trip_service.application.dtos.TripResponse;
import com.woundex.trip_service.application.dtos.UpdateStatusRequest;
import com.woundex.trip_service.domain.commands.AcceptTripCommand;
import com.woundex.trip_service.domain.commands.AssignDriverCommand;
import com.woundex.trip_service.domain.commands.CancelTripCommand;
import com.woundex.trip_service.domain.commands.CompleteTripCommand;
import com.woundex.trip_service.domain.commands.CreateTripCommand;
import com.woundex.trip_service.domain.commands.RateTripCommand;
import com.woundex.trip_service.domain.commands.StartTripCommand;
import com.woundex.trip_service.domain.commands.UpdateStatusCommand;
import com.woundex.trip_service.domain.value_object.DriverId;
import com.woundex.trip_service.domain.value_object.Location;
import com.woundex.trip_service.domain.value_object.RiderId;
import com.woundex.trip_service.domain.value_object.TripId;
import com.woundex.trip_service.domain.value_object.TripStatus;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripCommandHandler commandHandler;
    private final TripQueryHandler queryHandler;

    // ──────────────────────────────────────────────────────────
    //  POST /api/trips/request  –  Request a new trip
    // ──────────────────────────────────────────────────────────
    @PostMapping("/request")
    public ResponseEntity<?> requestTrip(@Valid @RequestBody CreateTripRequest request) {
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

    // ──────────────────────────────────────────────────────────
    //  POST /api/trips/{id}/accept  –  Driver accepts
    // ──────────────────────────────────────────────────────────
    @PostMapping("/{id}/accept")
    public ResponseEntity<?> acceptTrip(
        @PathVariable String id,
        @RequestParam UUID driverId
    ) {
        AcceptTripCommand command = new AcceptTripCommand(
            new TripId(id),
            new DriverId(driverId)
        );

        commandHandler.handle(command);

        return ResponseEntity.ok(Map.of("message", "Trip accepted by driver"));
    }

    // ──────────────────────────────────────────────────────────
    //  PUT /api/trips/{id}/status  –  Transition status
    // ──────────────────────────────────────────────────────────
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
        @PathVariable String id,
        @Valid @RequestBody UpdateStatusRequest request
    ) {
        TripStatus targetStatus = TripStatus.valueOf(request.status().toUpperCase());
        DriverId driverId = request.driverId() != null ? new DriverId(request.driverId()) : null;

        UpdateStatusCommand command = new UpdateStatusCommand(
            new TripId(id),
            targetStatus,
            driverId
        );

        commandHandler.handle(command);

        return ResponseEntity.ok(Map.of("message", "Trip status updated to " + targetStatus));
    }

    // ──────────────────────────────────────────────────────────
    //  GET /api/trips/{id}  –  Get trip details
    // ──────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getTripDetails(@PathVariable UUID id) {
        TripResponse trip = queryHandler.findById(id);
        return ResponseEntity.ok(trip);
    }

    // ──────────────────────────────────────────────────────────
    //  GET /api/trips/history  –  Rider / driver trip history
    //  Usage: ?riderId=... or ?driverId=...
    // ──────────────────────────────────────────────────────────
    @GetMapping("/history")
    public ResponseEntity<List<TripResponse>> getTripHistory(
        @RequestParam(required = false) UUID riderId,
        @RequestParam(required = false) UUID driverId
    ) {
        if (riderId != null) {
            return ResponseEntity.ok(queryHandler.findHistoryByRider(riderId));
        }
        if (driverId != null) {
            return ResponseEntity.ok(queryHandler.findHistoryByDriver(driverId));
        }
        return ResponseEntity.badRequest().build();
    }

    // ──────────────────────────────────────────────────────────
    //  POST /api/trips/{id}/rate  –  Submit rating
    // ──────────────────────────────────────────────────────────
    @PostMapping("/{id}/rate")
    public ResponseEntity<?> rateTrip(
        @PathVariable String id,
        @Valid @RequestBody RateTripRequest request
    ) {
        RateTripCommand command = new RateTripCommand(
            new TripId(id),
            request.rating()
        );

        commandHandler.handle(command);

        return ResponseEntity.ok(Map.of("message", "Rating submitted"));
    }

    // ──────────────────────────────────────────────────────────
    //  POST /api/trips/{id}/cancel  –  Cancel trip
    // ──────────────────────────────────────────────────────────
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelTrip(@PathVariable String id) {
        CancelTripCommand command = new CancelTripCommand(
            new TripId(id)
        );

        commandHandler.handle(command);

        return ResponseEntity.ok(Map.of("message", "Trip cancelled"));
    }

    // ──────────────────────────────────────────────────────────
    //  Legacy endpoints (backward compatibility with v1 paths)
    // ──────────────────────────────────────────────────────────

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
}