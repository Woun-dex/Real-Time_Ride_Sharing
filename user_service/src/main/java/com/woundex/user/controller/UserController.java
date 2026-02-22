package com.woundex.user.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.woundex.user.Services.UserService;
import com.woundex.user.dto.SignUpRequest;
import com.woundex.user.dto.UpdateProfileRequest;
import com.woundex.user.dto.UserProfileResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** POST /api/users/register */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody SignUpRequest request) {
        userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully"));
    }

    /** GET /api/users/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getProfile(id));
    }

    /** GET /api/users/me?userId={id} - frontend convenience alias */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfileMe(@RequestParam UUID userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    /** PUT /api/users/{id} */
    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponse> updateProfile(@PathVariable UUID id,
                                                             @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(id, request));
    }

    /** PUT /api/users/me?userId={id} - frontend convenience alias */
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfileMe(@RequestParam UUID userId,
                                                               @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    /** DELETE /api/users/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProfile(@PathVariable UUID id) {
        userService.softDeleteAccount(id);
        return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
    }

    /** DELETE /api/users/me?userId={id} - frontend convenience alias */
    @DeleteMapping("/me")
    public ResponseEntity<Map<String, String>> deleteProfileMe(@RequestParam UUID userId) {
        userService.softDeleteAccount(userId);
        return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
    }
}

