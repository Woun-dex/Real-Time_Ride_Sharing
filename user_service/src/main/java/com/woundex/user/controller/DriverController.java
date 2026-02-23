package com.woundex.user.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    /** PUT /api/drivers/{id}/status */
    @PutMapping("/{id}/status")
    public ResponseEntity<UserProfileResponse> updateStatus(@PathVariable UUID id,
                                                            @Valid @RequestBody DriverStatusRequest request) {
        return ResponseEntity.ok(userService.toggleDriverStatus(id, request));
    }
}
