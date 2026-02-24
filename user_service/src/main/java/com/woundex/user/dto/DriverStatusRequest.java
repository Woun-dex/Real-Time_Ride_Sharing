package com.woundex.user.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DriverStatusRequest {
    /** Optional until JWT auth is implemented; frontend may omit it. */
    private UUID driverId;

    /** Fallback identifier — driver's email address. */
    private String email;

    @NotBlank
    @Pattern(regexp = "ONLINE|OFFLINE", message = "Status must be ONLINE or OFFLINE")
    private String status;
}
