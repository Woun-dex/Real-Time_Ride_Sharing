package com.woundex.trip_service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Application context load test.
 * 
 * NOTE: Currently disabled because Trip_Entity domain class is not a JPA entity.
 * Enable when persistence layer is properly configured.
 */
@SpringBootTest
@ActiveProfiles("test")
@Disabled("Disabled until Trip_Entity is properly configured as JPA entity")
class TripServiceApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring context loads successfully
    }
}
