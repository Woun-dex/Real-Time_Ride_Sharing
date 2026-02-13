package com.woundex.gateway_service.flow;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Flow 3 – Trip Request → Driver Matching → Assignment
 * <p>
 * End-to-end path:
 * <pre>
 * Rider POST /api/trips/request
 *   → Trip Service creates trip (REQUESTED)
 *   → Kafka: trip.requested
 *   → Matching Service consumes → queries Location Service for nearby drivers
 *   → Kafka: driver.requested
 *   → Driver notified via WebSocket
 *   → Driver POST /api/trips/{id}/accept
 *   → Trip status → ACCEPTED
 *   → Kafka: trip.accepted
 *   → Rider notified via WebSocket (driver info + ETA)
 * </pre>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TripMatchingFlowTest {

    @Autowired
    private WebTestClient webTestClient;

    private final ReactorNettyWebSocketClient wsClient = new ReactorNettyWebSocketClient();
    private static String tripId;
    private static final String RIDER_TOKEN  = "RIDER_JWT";
    private static final String DRIVER_TOKEN = "DRIVER_JWT";

    /* ───────────── 1. Rider requests a trip ───────────── */

    @Test
    @Order(1)
    @DisplayName("POST /api/trips/request → 201 (REQUESTED)")
    void step1_riderRequestsTrip_shouldReturn201() {
        webTestClient.post()
            .uri("/api/trips/request")
            .header("Authorization", "Bearer " + RIDER_TOKEN)
            .header("Content-Type", "application/json")
            .bodyValue("""
                {
                    "riderId": "rider-001",
                    "pickup": {
                        "latitude": 40.7128,
                        "longitude": -74.0060,
                        "address": "123 Main St, New York"
                    },
                    "dropoff": {
                        "latitude": 40.7580,
                        "longitude": -73.9855,
                        "address": "Times Square, New York"
                    },
                    "rideType": "STANDARD"
                }
            """)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.tripId").isNotEmpty()
            .jsonPath("$.status").isEqualTo("REQUESTED")
            .jsonPath("$.estimatedFare").isNumber()
            .consumeWith(result -> {
                System.out.println("Trip created: " + new String(result.getResponseBody()));
            });
    }

    /* ───────────── 2. Driver receives notification (WS) ───────────── */

    @Test
    @Order(2)
    @DisplayName("Driver receives trip notification via WebSocket")
    void step2_driverReceivesTripNotification_viaWebSocket() {
        AtomicReference<String> receivedTrip = new AtomicReference<>();

        String wsUrl = "ws://localhost:8080/ws/driver/notifications?token=" + DRIVER_TOKEN;

        Mono<Void> result = wsClient.execute(
            URI.create(wsUrl),
            session -> session.receive()
                .take(1)
                .doOnNext(msg -> {
                    String payload = msg.getPayloadAsText();
                    System.out.println("Driver notified: " + payload);
                    receivedTrip.set(payload);
                })
                .then()
        );

        StepVerifier.create(result)
            .expectComplete()
            .verify(Duration.ofSeconds(30));

        Assertions.assertNotNull(receivedTrip.get(), "Driver should have received the notification");
        Assertions.assertTrue(receivedTrip.get().contains("tripId"),
            "Notification should contain tripId");
    }

    /* ───────────── 3. Driver accepts trip ───────────── */

    @Test
    @Order(3)
    @DisplayName("POST /api/trips/{id}/accept → ACCEPTED")
    void step3_driverAcceptsTrip_statusBecomesAccepted() {
        tripId = "trip-001"; // would come from step 1 in a real run

        webTestClient.post()
            .uri("/api/trips/" + tripId + "/accept")
            .header("Authorization", "Bearer " + DRIVER_TOKEN)
            .header("Content-Type", "application/json")
            .bodyValue("""
                { "driverId": "driver-001" }
            """)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("ACCEPTED")
            .jsonPath("$.driverId").isEqualTo("driver-001");
    }

    /* ───────────── 4. Rider receives driver assignment (WS) ───────────── */

    @Test
    @Order(4)
    @DisplayName("Rider receives driver-assigned notification via WebSocket")
    void step4_riderReceivesDriverAssignment_viaWebSocket() {
        String wsUrl = "ws://localhost:8080/ws/rider/notifications?token=" + RIDER_TOKEN;

        Mono<Void> result = wsClient.execute(
            URI.create(wsUrl),
            session -> session.receive()
                .take(1)
                .doOnNext(msg -> {
                    String payload = msg.getPayloadAsText();
                    System.out.println("Rider notified: " + payload);
                    Assertions.assertTrue(payload.contains("driverId"),
                        "Notification should contain driverId");
                })
                .then()
        );

        StepVerifier.create(result)
            .expectComplete()
            .verify(Duration.ofSeconds(15));
    }
}
