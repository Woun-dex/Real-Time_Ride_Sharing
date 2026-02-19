package com.woundex.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woundex.user.Services.UserService;
import com.woundex.user.dto.DriverStatusRequest;
import com.woundex.user.dto.UserProfileResponse;
import com.woundex.user.security.AuthPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final UserService userService;

    /**
     * PUT /api/drivers/status
     * Toggle driver availability ONLINE / OFFLINE.
     * Requires DRIVER role.
     */
    @PutMapping("/status")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<UserProfileResponse> updateStatus(@Valid @RequestBody DriverStatusRequest request,
                                                            Authentication auth) {
        AuthPrincipal principal = (AuthPrincipal) auth.getPrincipal();
        UserProfileResponse updated = userService.toggleDriverStatus(principal.getEmail(), request);
        return ResponseEntity.ok(updated);
    }
}
