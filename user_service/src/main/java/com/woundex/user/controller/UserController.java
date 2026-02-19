package com.woundex.user.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.woundex.user.Services.UserService;
import com.woundex.user.dto.SignUpRequest;
import com.woundex.user.dto.UpdateProfileRequest;
import com.woundex.user.dto.UserProfileResponse;
import com.woundex.user.security.AuthPrincipal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * POST /api/users/register
     * Register a new RIDER or DRIVER account.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody SignUpRequest request) {
        userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully"));
    }

    /**
     * GET /api/users/me
     * Return the authenticated user's profile.
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMe(Authentication auth) {
        AuthPrincipal principal = (AuthPrincipal) auth.getPrincipal();
        UserProfileResponse profile = userService.getProfile(principal.getEmail(), principal.getRole());
        return ResponseEntity.ok(profile);
    }

    /**
     * PUT /api/users/me
     * Update name / phone of the authenticated user.
     */
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMe(@RequestBody UpdateProfileRequest request,
                                                        Authentication auth) {
        AuthPrincipal principal = (AuthPrincipal) auth.getPrincipal();
        UserProfileResponse updated = userService.updateProfile(
                principal.getEmail(), principal.getRole(), request);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/users/me
     * Soft-delete the authenticated user's account.
     */
    @DeleteMapping("/me")
    public ResponseEntity<Map<String, String>> deleteMe(Authentication auth) {
        AuthPrincipal principal = (AuthPrincipal) auth.getPrincipal();
        userService.softDeleteAccount(principal.getEmail(), principal.getRole());
        return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
    }
}
