package com.woundex.trip_service.unit.domain;

import com.woundex.trip_service.domain.entities.Trip_Entity;
import com.woundex.trip_service.domain.value_object.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Trip Entity Unit Tests")
class TripEntityTest {

    @Nested
    @DisplayName("Trip Creation")
    class Creation {

        @Test
        @DisplayName("should create trip with valid data")
        void shouldCreateTripWithValidData() {
            // Given
            TripId tripId = TripId.generate();
            RiderId riderId = new RiderId(UUID.randomUUID());
            Location pickup = new Location(37.7749, -122.4194);
            Location destination = new Location(37.8049, -122.4294);

            // When
            Trip_Entity trip = Trip_Entity.create(tripId, riderId, pickup, destination);

            // Then
            assertThat(trip).isNotNull();
            assertThat(trip.getId()).isEqualTo(tripId);
            assertThat(trip.getRiderId()).isEqualTo(riderId);
            assertThat(trip.getPickup()).isEqualTo(pickup);
            assertThat(trip.getDestination()).isEqualTo(destination);
            assertThat(trip.getStatus()).isEqualTo(TripStatus.REQUESTED);
            assertThat(trip.getDriverId()).isEmpty();
            assertThat(trip.getDomainEvents()).hasSize(1);
        }

        @Test
        @DisplayName("should throw exception when pickup equals destination")
        void shouldThrowWhenPickupEqualsDestination() {
            // Given
            TripId tripId = TripId.generate();
            RiderId riderId = new RiderId(UUID.randomUUID());
            Location sameLocation = new Location(37.7749, -122.4194);

            // When/Then
            assertThatThrownBy(() -> 
                Trip_Entity.create(tripId, riderId, sameLocation, sameLocation)
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("pickup and destination must differ");
        }

        @Test
        @DisplayName("should throw exception when riderId is null")
        void shouldThrowWhenRiderIdIsNull() {
            // Given
            TripId tripId = TripId.generate();
            Location pickup = new Location(37.7749, -122.4194);
            Location destination = new Location(37.8049, -122.4294);

            // When/Then
            assertThatThrownBy(() -> 
                Trip_Entity.create(tripId, null, pickup, destination)
            )
            .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Driver Assignment")
    class DriverAssignment {

        @Test
        @DisplayName("should assign driver to requested trip")
        void shouldAssignDriverToRequestedTrip() {
            // Given
            Trip_Entity trip = createTrip();
            DriverId driverId = new DriverId(UUID.randomUUID());

            // When
            trip.assignDriver(driverId);

            // Then
            assertThat(trip.getStatus()).isEqualTo(TripStatus.ASSIGNED);
            assertThat(trip.getDriverId()).isPresent().contains(driverId);
            assertThat(trip.getDomainEvents()).hasSize(2); // TripRequested + DriverAssigned
        }

        @Test
        @DisplayName("should throw when assigning driver to non-requested trip")
        void shouldThrowWhenAssigningDriverToNonRequestedTrip() {
            // Given
            Trip_Entity trip = createTrip();
            DriverId driverId = new DriverId(UUID.randomUUID());
            trip.assignDriver(driverId); // Now ASSIGNED

            // When/Then
            assertThatThrownBy(() -> trip.assignDriver(new DriverId(UUID.randomUUID())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Trip not assignable");
        }
    }

    @Nested
    @DisplayName("Trip Start")
    class TripStart {

        @Test
        @DisplayName("should start trip when driver is assigned")
        void shouldStartTripWhenDriverAssigned() {
            // Given
            Trip_Entity trip = createTrip();
            DriverId driverId = new DriverId(UUID.randomUUID());
            trip.assignDriver(driverId);
            trip.clearEvents();

            // When
            trip.start();

            // Then
            assertThat(trip.getStatus()).isEqualTo(TripStatus.IN_PROGRESS);
            assertThat(trip.getDomainEvents()).hasSize(1);
        }

        @Test
        @DisplayName("should throw when starting trip without driver assigned")
        void shouldThrowWhenStartingWithoutDriverAssigned() {
            // Given
            Trip_Entity trip = createTrip();

            // When/Then
            assertThatThrownBy(() -> trip.start())
                .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("Trip Completion")
    class TripCompletion {

        @Test
        @DisplayName("should complete in-progress trip")
        void shouldCompleteInProgressTrip() {
            // Given
            Trip_Entity trip = createAndStartTrip();
            trip.clearEvents();

            // When
            trip.complete();

            // Then
            assertThat(trip.getStatus()).isEqualTo(TripStatus.COMPLETED);
            assertThat(trip.getDomainEvents()).hasSize(1);
        }

        @Test
        @DisplayName("should throw when completing non-in-progress trip")
        void shouldThrowWhenCompletingNonInProgressTrip() {
            // Given
            Trip_Entity trip = createTrip();

            // When/Then
            assertThatThrownBy(() -> trip.complete())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Trip not completable");
        }
    }

    @Nested
    @DisplayName("Trip Cancellation")
    class TripCancellation {

        @Test
        @DisplayName("should cancel requested trip")
        void shouldCancelRequestedTrip() {
            // Given
            Trip_Entity trip = createTrip();
            trip.clearEvents();

            // When
            trip.cancel();

            // Then
            assertThat(trip.getStatus()).isEqualTo(TripStatus.CANCELLED);
        }

        @Test
        @DisplayName("should cancel assigned trip")
        void shouldCancelAssignedTrip() {
            // Given
            Trip_Entity trip = createTrip();
            trip.assignDriver(new DriverId(UUID.randomUUID()));
            trip.clearEvents();

            // When
            trip.cancel();

            // Then
            assertThat(trip.getStatus()).isEqualTo(TripStatus.CANCELLED);
        }

        @Test
        @DisplayName("should throw when cancelling completed trip")
        void shouldThrowWhenCancellingCompletedTrip() {
            // Given
            Trip_Entity trip = createAndCompleteTrip();

            // When/Then
            assertThatThrownBy(() -> trip.cancel())
                .isInstanceOf(IllegalStateException.class);
        }
    }

    // ============ Helper Methods ============
    
    private Trip_Entity createTrip() {
        return Trip_Entity.create(
            TripId.generate(),
            new RiderId(UUID.randomUUID()),
            new Location(37.7749, -122.4194),
            new Location(37.8049, -122.4294)
        );
    }

    private Trip_Entity createAndStartTrip() {
        Trip_Entity trip = createTrip();
        trip.assignDriver(new DriverId(UUID.randomUUID()));
        trip.start();
        return trip;
    }

    private Trip_Entity createAndCompleteTrip() {
        Trip_Entity trip = createAndStartTrip();
        trip.complete();
        return trip;
    }
}
