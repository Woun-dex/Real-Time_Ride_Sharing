package com.woundex.gateway_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

/**
 * Fallback controller for circuit-breaker fallbacks.
 * Returns a meaningful response when a downstream service is unavailable.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/{serviceName}")
    public Mono<Map<String, Object>> fallback(@PathVariable String serviceName) {
        return Mono.just(Map.of(
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "error", "Service Unavailable",
                "message", serviceName + " is currently unavailable. Please try again later.",
                "timestamp", Instant.now().toString()
        ));
    }
}
