package com.woundex.gateway_service.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Programmatic route configuration (supplements application.yaml routes).
 * Add custom route logic here when YAML-based config is not sufficient.
 */
@Configuration
public class GatewayRouteConfig {

    /**
     * Custom routes that require programmatic filters or predicates.
     * The main routes are declared in application.yaml for easy tuning.
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Health-check shortcut: GET /health → actuator
                .route("gateway-health", r -> r
                        .path("/health")
                        .uri("forward:///actuator/health"))
                .build();
    }
}
