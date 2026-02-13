package com.woundex.gateway_service.flow;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.time.Duration;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WebSocketLocationFlowTest {

    private final ReactorNettyWebSocketClient client = new ReactorNettyWebSocketClient();

    /* ───────────── 1. Driver sends location, receives ACK ───────────── */

    @Test
    @Order(1)
    @DisplayName("Driver sends 3 GPS updates → receives 3 ACKs")
    void driverSendsLocation_shouldReceiveAck() {
        String wsUrl = "ws://localhost:8080/ws/location?token=DRIVER_JWT_TOKEN";

        Mono<Void> result = client.execute(
            URI.create(wsUrl),
            session -> {
                // Send 3 location updates, 2 s apart
                Flux<WebSocketMessage> outbound = Flux.interval(Duration.ofSeconds(2))
                    .take(3)
                    .map(i -> session.textMessage(String.format("""
                        {
                            "driverId": "driver-001",
                            "latitude": %.4f,
                            "longitude": -74.0060,
                            "heading": 90.0,
                            "speed": 35.5,
                            "timestamp": %d
                        }
                    """, 40.7128 + (i * 0.001), System.currentTimeMillis())));

                Mono<Void> inbound = session.receive()
                    .take(3)
                    .doOnNext(msg -> {
                        String payload = msg.getPayloadAsText();
                        System.out.println("ACK received: " + payload);
                        Assertions.assertTrue(payload.contains("received") || payload.contains("ack"),
                            "Response should contain acknowledgement");
                    })
                    .then();

                return session.send(outbound).thenMany(inbound).then();
            }
        );

        StepVerifier.create(result)
            .expectComplete()
            .verify(Duration.ofSeconds(15));
    }

    /* ───────────── 2. Rider tracks driver via WebSocket ───────────── */

    @Test
    @Order(2)
    @DisplayName("Rider subscribes to trip channel → receives driver positions")
    void riderTracksDriver_shouldReceiveLocationUpdates() {
        String tripId = "trip-001";
        String wsUrl = "ws://localhost:8080/ws/track/" + tripId + "?token=RIDER_JWT_TOKEN";

        Mono<Void> result = client.execute(
            URI.create(wsUrl),
            session -> session.receive()
                .take(3)
                .doOnNext(msg -> {
                    String payload = msg.getPayloadAsText();
                    System.out.println("Location update: " + payload);
                    Assertions.assertTrue(payload.contains("latitude"),
                        "Payload should contain latitude field");
                })
                .then()
        );

        StepVerifier.create(result)
            .expectComplete()
            .verify(Duration.ofSeconds(20));
    }
}
