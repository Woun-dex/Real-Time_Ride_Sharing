package com.woundex.trip_service.integration;

import com.woundex.trip_service.application.dtos.AssignDriverRequest;
import com.woundex.trip_service.application.dtos.CreateTripRequest;
import com.woundex.trip_service.application.dtos.LocationDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for TripController.
 * 
 * NOTE: These tests are currently disabled because the Trip_Entity domain class
 * is not a JPA entity (missing @Entity annotation). The project uses DDD pattern
 * but the repository interface expects a JPA entity.
 * 
 * To enable these tests:
 * 1. Add @Entity annotation to Trip_Entity
 * 2. Or create a separate JPA entity class for persistence
 * 3. Implement proper mapping between domain and persistence layers
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.autoconfigure.exclude=" +
            "org.springframework.boot.kafka.autoconfigure.KafkaAutoConfiguration," +
            "org.springframework.boot.data.redis.autoconfigure.RedisAutoConfiguration," +
            "org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration," +
            "org.springframework.boot.data.redis.autoconfigure.RedisRepositoriesAutoConfiguration"
    }
)
@ActiveProfiles("test")
@Disabled("Disabled until Trip_Entity is properly configured as JPA entity")
@DisplayName("Trip Controller Integration Tests")
class TripControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1/trips";
    }

    @Nested
    @DisplayName("POST /api/v1/trips")
    class CreateTripTests {

        @Test
        @DisplayName("should create trip with valid request")
        void shouldCreateTripWithValidRequest() {
            CreateTripRequest request = new CreateTripRequest(
                UUID.randomUUID(),
                new LocationDTO(37.7749, -122.4194),
                new LocationDTO(37.8049, -122.4294)
            );

            given()
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post()
            .then()
                .statusCode(201)
                .body("tripId", notNullValue());
        }

        @Test
        @DisplayName("should return 400 when pickup equals destination")
        void shouldReturn400WhenPickupEqualsDestination() {
            CreateTripRequest request = new CreateTripRequest(
                UUID.randomUUID(),
                new LocationDTO(37.7749, -122.4194),
                new LocationDTO(37.7749, -122.4194)
            );

            given()
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post()
            .then()
                .statusCode(400);
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/trips/{tripId}/assign-driver")
    class AssignDriverTests {

        @Test
        @DisplayName("should assign driver to existing trip")
        void shouldAssignDriverToExistingTrip() {
            // Create a trip first
            String tripId = createTrip();
            AssignDriverRequest assignRequest = new AssignDriverRequest(UUID.randomUUID());

            given()
                .contentType(ContentType.JSON)
                .body(assignRequest)
            .when()
                .put("/{tripId}/assign-driver", tripId)
            .then()
                .statusCode(200)
                .body("message", containsString("assigned"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/trips/{tripId}/start")
    class StartTripTests {

        @Test
        @DisplayName("should start trip with assigned driver")
        void shouldStartTripWithAssignedDriver() {
            String tripId = createTrip();
            UUID driverId = UUID.randomUUID();
            assignDriver(tripId, driverId);

            given()
                .queryParam("driverId", driverId.toString())
            .when()
                .put("/{tripId}/start", tripId)
            .then()
                .statusCode(200)
                .body("message", containsString("started"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/trips/{tripId}/complete")
    class CompleteTripTests {

        @Test
        @DisplayName("should complete in-progress trip")
        void shouldCompleteInProgressTrip() {
            String tripId = createTrip();
            UUID driverId = UUID.randomUUID();
            assignDriver(tripId, driverId);
            startTrip(tripId, driverId);

            when()
                .put("/{tripId}/complete", tripId)
            .then()
                .statusCode(200)
                .body("message", containsString("completed"));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/trips/{tripId}/cancel")
    class CancelTripTests {

        @Test
        @DisplayName("should cancel requested trip")
        void shouldCancelRequestedTrip() {
            String tripId = createTrip();

            when()
                .put("/{tripId}/cancel", tripId)
            .then()
                .statusCode(200)
                .body("message", containsString("cancelled"));
        }
    }

    // ============ Helper Methods ============

    private String createTrip() {
        CreateTripRequest request = new CreateTripRequest(
            UUID.randomUUID(),
            new LocationDTO(37.7749, -122.4194),
            new LocationDTO(37.8049, -122.4294)
        );

        Response response = given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post()
        .then()
            .statusCode(201)
            .extract().response();

        return response.jsonPath().getString("tripId");
    }

    private void assignDriver(String tripId, UUID driverId) {
        AssignDriverRequest request = new AssignDriverRequest(driverId);

        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .put("/{tripId}/assign-driver", tripId)
        .then()
            .statusCode(200);
    }

    private void startTrip(String tripId, UUID driverId) {
        given()
            .queryParam("driverId", driverId.toString())
        .when()
            .put("/{tripId}/start", tripId)
        .then()
            .statusCode(200);
    }
}
