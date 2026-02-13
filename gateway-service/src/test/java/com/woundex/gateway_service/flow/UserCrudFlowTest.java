package com.woundex.gateway_service.flow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserCrudFlowTest {

    @Autowired
    private WebTestClient webTestClient;

    private static String jwtToken;
    private static String userId;

    /* ───────────── 1. Register Rider ───────────── */

    @Test
    @Order(1)
    @DisplayName("POST /api/users/register  → 201 (Rider)")
    void registerRider_shouldReturn201() {
        webTestClient.post()
            .uri("/api/users/register")
            .bodyValue("""
                {
                    "name": "John Rider",
                    "email": "john@test.com",
                    "phone": "+1234567890",
                    "password": "securePass123",
                    "role": "RIDER"
                }
            """)
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.token").isNotEmpty()
            .jsonPath("$.userId").isNotEmpty()
            .consumeWith(result -> {
                // In a real test, parse the body and store jwtToken + userId
                System.out.println("Rider registered: " + new String(result.getResponseBody()));
            });
    }

    /* ───────────── 2. Register Driver ───────────── */

    @Test
    @Order(2)
    @DisplayName("POST /api/users/register  → 201 (Driver)")
    void registerDriver_shouldReturn201() {
        webTestClient.post()
            .uri("/api/users/register")
            .bodyValue("""
                {
                    "name": "Jane Driver",
                    "email": "jane@test.com",
                    "phone": "+0987654321",
                    "password": "securePass456",
                    "role": "DRIVER",
                    "vehicleInfo": {
                        "make": "Toyota",
                        "model": "Camry",
                        "year": 2023,
                        "plateNumber": "ABC-1234",
                        "color": "White"
                    }
                }
            """)
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.token").isNotEmpty()
            .jsonPath("$.userId").isNotEmpty();
    }

    /* ───────────── 3. Duplicate e-mail ───────────── */

    @Test
    @Order(3)
    @DisplayName("POST /api/users/register (dup email) → 409")
    void duplicateEmail_shouldReturn409() {
        webTestClient.post()
            .uri("/api/users/register")
            .bodyValue("""
                {
                    "name": "John Duplicate",
                    "email": "john@test.com",
                    "phone": "+1111111111",
                    "password": "pass",
                    "role": "RIDER"
                }
            """)
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isEqualTo(409);
    }

    /* ───────────── 4. Get profile ───────────── */

    @Test
    @Order(4)
    @DisplayName("GET /api/users/me → 200")
    void getProfile_shouldReturnUser() {
        webTestClient.get()
            .uri("/api/users/me")
            .header("Authorization", "Bearer " + jwtToken)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.name").isEqualTo("John Rider")
            .jsonPath("$.role").isEqualTo("RIDER");
    }

    /* ───────────── 5. Update profile ───────────── */

    @Test
    @Order(5)
    @DisplayName("PUT /api/users/me → 200")
    void updateProfile_shouldReflectChanges() {
        webTestClient.put()
            .uri("/api/users/me")
            .header("Authorization", "Bearer " + jwtToken)
            .bodyValue("""
                { "name": "John Updated" }
            """)
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.name").isEqualTo("John Updated");
    }
}
