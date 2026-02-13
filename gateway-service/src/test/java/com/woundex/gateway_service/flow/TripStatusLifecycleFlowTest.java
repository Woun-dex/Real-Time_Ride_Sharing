package com.woundex.gateway_service.flow;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Flow 4 – Trip Status Lifecycle (Event-Driven)
 * <p>
 * Each status transition publishes a Kafka event that downstream services
 * (Notification Service, Payment Service) react to.
 *
 * <h3>Valid state machine</h3>
 * <pre>
 * REQUESTED → MATCHED → ACCEPTED → DRIVER_EN_ROUTE → ARRIVED
 *      ↓                                                  ↓
 *  (timeout)                                        IN_PROGRESS → COMPLETED
 *      ↓                                                              ↓
 *  CANCELLED                                               PAYMENT_PROCESSED
 * </pre>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TripStatusLifecycleFlowTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final String TRIP_ID      = "trip-001";
    private static final String DRIVER_TOKEN = "DRIVER_JWT";
    private static final String RIDER_TOKEN  = "RIDER_JWT";

    /* ───────────── 1. ACCEPTED → DRIVER_EN_ROUTE ───────────── */

    @Test
    @Order(1)
    @DisplayName("Driver starts en route → status changes + ETA returned")
    void driverStartsEnRoute_statusChanges() {
        updateStatus("DRIVER_EN_ROUTE")
            .jsonPath("$.eta").isNotEmpty();
    }

    /* ───────────── 2. DRIVER_EN_ROUTE → ARRIVED ───────────── */

    @Test
    @Order(2)
    @DisplayName("Driver arrives at pickup → status = ARRIVED")
    void driverArrives_statusChanges() {
        updateStatus("ARRIVED");
    }

    /* ───────────── 3. ARRIVED → IN_PROGRESS ───────────── */

    @Test
    @Order(3)
    @DisplayName("Trip starts → startTime is set")
    void tripStarts_statusChanges() {
        updateStatus("IN_PROGRESS")
            .jsonPath("$.startTime").isNotEmpty();
    }

    /* ───────────── 4. IN_PROGRESS → COMPLETED ───────────── */

    @Test
    @Order(4)
    @DisplayName("Trip completed → fare, distance, duration returned")
    void tripCompletes_fareCalculated() {
        webTestClient.put()
            .uri("/api/trips/" + TRIP_ID + "/status")
            .header("Authorization", "Bearer " + DRIVER_TOKEN)
            .header("Content-Type", "application/json")
            .bodyValue("""
                {
                    "status": "COMPLETED",
                    "endLocation": {
                        "latitude": 40.7580,
                        "longitude": -73.9855
                    }
                }
            """)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("COMPLETED")
            .jsonPath("$.fare").isNumber()
            .jsonPath("$.distance").isNumber()
            .jsonPath("$.duration").isNotEmpty();
    }

    /* ───────────── 5. Invalid transition ───────────── */

    @Test
    @Order(5)
    @DisplayName("COMPLETED → IN_PROGRESS is invalid → 400")
    void invalidTransition_shouldReturn400() {
        webTestClient.put()
            .uri("/api/trips/" + TRIP_ID + "/status")
            .header("Authorization", "Bearer " + DRIVER_TOKEN)
            .header("Content-Type", "application/json")
            .bodyValue("""
                { "status": "IN_PROGRESS" }
            """)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.error").isEqualTo("Invalid status transition");
    }

    /* ───────────── 6. Trip history ───────────── */

    @Test
    @Order(6)
    @DisplayName("GET /api/trips/history → completed trip visible")
    void riderViewsTripHistory_shouldReturnCompleted() {
        webTestClient.get()
            .uri("/api/trips/history?riderId=rider-001")
            .header("Authorization", "Bearer " + RIDER_TOKEN)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$[0].tripId").isEqualTo(TRIP_ID)
            .jsonPath("$[0].status").isEqualTo("COMPLETED")
            .jsonPath("$[0].fare").isNumber();
    }

    /* ── Helper ── */

    private WebTestClient.BodyContentSpec updateStatus(String status) {
        return webTestClient.put()
            .uri("/api/trips/" + TRIP_ID + "/status")
            .header("Authorization", "Bearer " + DRIVER_TOKEN)
            .header("Content-Type", "application/json")
            .bodyValue("{\"status\":\"" + status + "\"}")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo(status);
    }
}
