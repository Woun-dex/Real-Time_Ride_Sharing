package com.woundex.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class DriverStatusRequest {
    @NotBlank
    @Pattern(regexp = "ONLINE|OFFLINE", message = "Status must be ONLINE or OFFLINE")
    private String status;
}
