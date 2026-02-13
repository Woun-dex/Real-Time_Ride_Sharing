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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FullEndToEndFlowTest {

    @Autowired
    private WebTestClient webTestClient;

    private final ReactorNettyWebSocketClient wsClient = new ReactorNettyWebSocketClient();

    private static String riderToken;
    private static String driverToken;
    private static String tripId;

    // ═════════════════════════════════════════════════════
    //  PHASE 1 – USER REGISTRATION
    // ═════════════════════════════════════════════════════

    @Test @Order(1)
    @DisplayName("Phase 1.1 – Register Rider")
    void registerRider() {
        webTestClient.post().uri("/api/users/register")
            .header("Content-Type", "application/json")
            .bodyValue("""
                {
                    "name":"Alice","email":"alice@test.com",
                    "password":"pass123","role":"RIDER","phone":"+1000000001"
                }
            """)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.token").value(token -> riderToken = (String) token);
    }

    @Test @Order(2)
    @DisplayName("Phase 1.2 – Register Driver")
    void registerDriver() {
        webTestClient.post().uri("/api/users/register")
            .header("Content-Type", "application/json")
            .bodyValue("""
                {
                    "name":"Bob","email":"bob@test.com",
                    "password":"pass456","role":"DRIVER","phone":"+1000000002",
                    "vehicleInfo":{
                        "make":"Honda","model":"Civic","year":2024,
                        "plateNumber":"XYZ-789","color":"Blue"
                    }
                }
            """)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.token").value(token -> driverToken = (String) token);
    }

    // ═════════════════════════════════════════════════════
    //  PHASE 2 – DRIVER GOES ONLINE + SENDS LOCATION
    // ═════════════════════════════════════════════════════

    @Test @Order(3)
    @DisplayName("Phase 2.1 – Driver goes online")
    void driverGoesOnline() {
        webTestClient.put().uri("/api/drivers/status")
            .header("Authorization", "Bearer " + driverToken)
            .header("Content-Type", "application/json")
            .bodyValue("""
                { "status": "ONLINE" }
            """)
            .exchange()
            .expectStatus().isOk();
    }

    @Test @Order(4)
    @DisplayName("Phase 2.2 – Driver sends GPS via WebSocket")
    void driverSendsLocation() {
        Mono<Void> result = wsClient.execute(
            URI.create("ws://localhost:8080/ws/location?token=" + driverToken),
            session -> {
                var msg = session.textMessage("""
                    {
                        "driverId":"driver-001",
                        "latitude":40.7128,"longitude":-74.0060,
                        "speed":0,"heading":0
                    }
                """);
                return session.send(Mono.just(msg))
                    .thenMany(session.receive().take(1))
                    .then();
            }
        );
        StepVerifier.create(result).expectComplete().verify(Duration.ofSeconds(10));
    }

    // ═════════════════════════════════════════════════════
    //  PHASE 3 – RIDER REQUESTS TRIP
    // ═════════════════════════════════════════════════════

    @Test @Order(5)
    @DisplayName("Phase 3 – Rider requests a trip")
    void riderRequestsTrip() {
        webTestClient.post().uri("/api/trips/request")
            .header("Authorization", "Bearer " + riderToken)
            .header("Content-Type", "application/json")
            .bodyValue("""
                {
                    "pickup":  {"latitude":40.7128,"longitude":-74.0060,"address":"NYC Downtown"},
                    "dropoff": {"latitude":40.7580,"longitude":-73.9855,"address":"Times Square"},
                    "rideType":"STANDARD"
                }
            """)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.tripId").value(id -> tripId = (String) id)
            .jsonPath("$.status").isEqualTo("REQUESTED");
    }

    // ═════════════════════════════════════════════════════
    //  PHASE 4 – DRIVER NOTIFIED + ACCEPTS
    // ═════════════════════════════════════════════════════

    @Test @Order(6)
    @DisplayName("Phase 4.1 – Driver receives trip notification via WS")
    void driverReceivesNotification() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> notification = new AtomicReference<>();

        wsClient.execute(
            URI.create("ws://localhost:8080/ws/driver/notifications?token=" + driverToken),
            session -> session.receive()
                .take(1)
                .doOnNext(msg -> {
                    notification.set(msg.getPayloadAsText());
                    latch.countDown();
                })
                .then()
        ).subscribe();

        Assertions.assertTrue(latch.await(30, TimeUnit.SECONDS),
            "Driver should receive trip notification within 30 s");
        Assertions.assertTrue(notification.get().contains("tripId"),
            "Notification payload must contain tripId");
    }

    @Test @Order(7)
    @DisplayName("Phase 4.2 – Driver accepts the trip")
    void driverAcceptsTrip() {
        webTestClient.post().uri("/api/trips/" + tripId + "/accept")
            .header("Authorization", "Bearer " + driverToken)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("ACCEPTED");
    }

    // ═════════════════════════════════════════════════════
    //  PHASE 5 – TRIP STATUS LIFECYCLE
    // ═════════════════════════════════════════════════════

    @Test @Order(8)
    @DisplayName("Phase 5.1 – Driver en route")
    void driverEnRoute() { transitionTo("DRIVER_EN_ROUTE"); }

    @Test @Order(9)
    @DisplayName("Phase 5.2 – Driver arrived at pickup")
    void driverArrived() { transitionTo("ARRIVED"); }

    @Test @Order(10)
    @DisplayName("Phase 5.3 – Trip in progress")
    void tripStarted() { transitionTo("IN_PROGRESS"); }

    @Test @Order(11)
    @DisplayName("Phase 5.4 – Trip completed (fare calculated)")
    void tripCompleted() {
        webTestClient.put().uri("/api/trips/" + tripId + "/status")
            .header("Authorization", "Bearer " + driverToken)
            .header("Content-Type", "application/json")
            .bodyValue("""
                {
                    "status":"COMPLETED",
                    "endLocation":{"latitude":40.7580,"longitude":-73.9855}
                }
            """)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("COMPLETED")
            .jsonPath("$.fare").isNumber();
    }

    // ═════════════════════════════════════════════════════
    //  PHASE 6 – RIDER RATES DRIVER
    // ═════════════════════════════════════════════════════

    @Test @Order(12)
    @DisplayName("Phase 6 – Rider rates the trip")
    void riderRatesTrip() {
        webTestClient.post().uri("/api/trips/" + tripId + "/rate")
            .header("Authorization", "Bearer " + riderToken)
            .header("Content-Type", "application/json")
            .bodyValue("""
                {"rating": 5, "comment": "Great ride!"}
            """)
            .exchange()
            .expectStatus().isOk();
    }

    // ── Helper ──

    private void transitionTo(String status) {
        webTestClient.put().uri("/api/trips/" + tripId + "/status")
            .header("Authorization", "Bearer " + driverToken)
            .header("Content-Type", "application/json")
            .bodyValue("{\"status\":\"" + status + "\"}")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo(status);
    }
}
