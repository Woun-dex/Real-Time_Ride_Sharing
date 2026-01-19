package com.woundex.trip_service.unit.application;

import com.woundex.trip_service.application.Commands.TripCommandHandler;
import com.woundex.trip_service.domain.commands.*;
import com.woundex.trip_service.domain.entities.Trip_Entity;
import com.woundex.trip_service.domain.value_object.*;
import com.woundex.trip_service.infrastructure.messaging.DomainEventPublisher;
import com.woundex.trip_service.infrastructure.persistence.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TripCommandHandler Unit Tests")
class TripCommandHandlerTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private TripCommandHandler commandHandler;

    @Captor
    private ArgumentCaptor<Trip_Entity> tripCaptor;

    @Nested
    @DisplayName("CreateTripCommand")
    class CreateTripCommandTests {

        @Test
        @DisplayName("should create trip and return trip id")
        void shouldCreateTripAndReturnTripId() {
            // Given
            CreateTripCommand command = new CreateTripCommand(
                new RiderId(UUID.randomUUID()),
                new Location(37.7749, -122.4194),
                new Location(37.8049, -122.4294)
            );

            // When
            TripId result = commandHandler.handle(command);

            // Then
            assertThat(result).isNotNull();
            verify(tripRepository).save(tripCaptor.capture());
            verify(domainEventPublisher).publish(any());
            
            Trip_Entity savedTrip = tripCaptor.getValue();
            assertThat(savedTrip.getStatus()).isEqualTo(TripStatus.REQUESTED);
            assertThat(savedTrip.getRiderId()).isEqualTo(command.riderId());
        }
    }

    @Nested
    @DisplayName("AssignDriverCommand")
    class AssignDriverCommandTests {

        @Test
        @DisplayName("should assign driver to trip")
        void shouldAssignDriverToTrip() {
            // Given
            TripId tripId = TripId.generate();
            DriverId driverId = new DriverId(UUID.randomUUID());
            Trip_Entity existingTrip = Trip_Entity.create(
                tripId,
                new RiderId(UUID.randomUUID()),
                new Location(37.7749, -122.4194),
                new Location(37.8049, -122.4294)
            );
            existingTrip.clearEvents();

            when(tripRepository.findById(tripId)).thenReturn(Optional.of(existingTrip));

            AssignDriverCommand command = new AssignDriverCommand(tripId, driverId);

            // When
            commandHandler.handle(command);

            // Then
            verify(tripRepository).save(tripCaptor.capture());
            Trip_Entity savedTrip = tripCaptor.getValue();
            assertThat(savedTrip.getStatus()).isEqualTo(TripStatus.ASSIGNED);
            assertThat(savedTrip.getDriverId()).contains(driverId);
        }

        @Test
        @DisplayName("should throw when trip not found")
        void shouldThrowWhenTripNotFound() {
            // Given
            TripId tripId = TripId.generate();
            when(tripRepository.findById(tripId)).thenReturn(Optional.empty());

            AssignDriverCommand command = new AssignDriverCommand(tripId, new DriverId(UUID.randomUUID()));

            // When/Then
            assertThatThrownBy(() -> commandHandler.handle(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trip not found");
        }
    }

    @Nested
    @DisplayName("StartTripCommand")
    class StartTripCommandTests {

        @Test
        @DisplayName("should start trip when driver matches")
        void shouldStartTripWhenDriverMatches() {
            // Given
            TripId tripId = TripId.generate();
            DriverId driverId = new DriverId(UUID.randomUUID());
            Trip_Entity trip = Trip_Entity.create(
                tripId,
                new RiderId(UUID.randomUUID()),
                new Location(37.7749, -122.4194),
                new Location(37.8049, -122.4294)
            );
            trip.assignDriver(driverId);
            trip.clearEvents();

            when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

            StartTripCommand command = new StartTripCommand(tripId, driverId);

            // When
            commandHandler.handle(command);

            // Then
            verify(tripRepository).save(tripCaptor.capture());
            assertThat(tripCaptor.getValue().getStatus()).isEqualTo(TripStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("should throw when driver does not match")
        void shouldThrowWhenDriverDoesNotMatch() {
            // Given
            TripId tripId = TripId.generate();
            DriverId assignedDriver = new DriverId(UUID.randomUUID());
            DriverId wrongDriver = new DriverId(UUID.randomUUID());
            
            Trip_Entity trip = Trip_Entity.create(
                tripId,
                new RiderId(UUID.randomUUID()),
                new Location(37.7749, -122.4194),
                new Location(37.8049, -122.4294)
            );
            trip.assignDriver(assignedDriver);

            when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

            StartTripCommand command = new StartTripCommand(tripId, wrongDriver);

            // When/Then
            assertThatThrownBy(() -> commandHandler.handle(command))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Driver mismatch");
        }
    }

    @Nested
    @DisplayName("CompleteTripCommand")
    class CompleteTripCommandTests {

        @Test
        @DisplayName("should complete in-progress trip")
        void shouldCompleteInProgressTrip() {
            // Given
            TripId tripId = TripId.generate();
            Trip_Entity trip = Trip_Entity.create(
                tripId,
                new RiderId(UUID.randomUUID()),
                new Location(37.7749, -122.4194),
                new Location(37.8049, -122.4294)
            );
            trip.assignDriver(new DriverId(UUID.randomUUID()));
            trip.start();
            trip.clearEvents();

            when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

            CompleteTripCommand command = new CompleteTripCommand(tripId);

            // When
            commandHandler.handle(command);

            // Then
            verify(tripRepository).save(tripCaptor.capture());
            assertThat(tripCaptor.getValue().getStatus()).isEqualTo(TripStatus.COMPLETED);
        }
    }

    @Nested
    @DisplayName("CancelTripCommand")
    class CancelTripCommandTests {

        @Test
        @DisplayName("should cancel trip")
        void shouldCancelTrip() {
            // Given
            TripId tripId = TripId.generate();
            Trip_Entity trip = Trip_Entity.create(
                tripId,
                new RiderId(UUID.randomUUID()),
                new Location(37.7749, -122.4194),
                new Location(37.8049, -122.4294)
            );
            trip.clearEvents();

            when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

            CancelTripCommand command = new CancelTripCommand(tripId);

            // When
            commandHandler.handle(command);

            // Then
            verify(tripRepository).save(tripCaptor.capture());
            assertThat(tripCaptor.getValue().getStatus()).isEqualTo(TripStatus.CANCELLED);
        }
    }
}
