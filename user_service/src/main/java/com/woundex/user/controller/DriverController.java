package com.woundex.user.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woundex.user.Services.UserService;
import com.woundex.user.dto.DriverStatusRequest;
import com.woundex.user.dto.UserProfileResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final UserService userService;

    /**
     * PUT /api/drivers/status
     * Accepts driverId from (in priority order):
     *   1. JSON body field "driverId"
     *   2. X-Driver-Id request header
     *   3. JSON body field "email" (looked up in DB)
     */
    @PutMapping("/status")
    public ResponseEntity<UserProfileResponse> updateStatus(
            @Valid @RequestBody DriverStatusRequest request,
            @RequestHeader(value = "X-Driver-Id", required = false) String driverIdHeader) {

        UUID driverId = resolveDriverId(request, driverIdHeader);
        return ResponseEntity.ok(userService.toggleDriverStatus(driverId, request));
    }

    private UUID resolveDriverId(DriverStatusRequest request, String driverIdHeader) {
        // 1. Body field
        if (request.getDriverId() != null) {
            return request.getDriverId();
        }
        // 2. Header
        if (driverIdHeader != null && !driverIdHeader.isBlank()) {
            return UUID.fromString(driverIdHeader);
        }
        // 3. Email lookup
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            return userService.findDriverIdByEmail(request.getEmail());
        }
        throw new IllegalArgumentException(
                "Cannot identify driver: provide driverId in the body, X-Driver-Id header, or email");
    }
}
